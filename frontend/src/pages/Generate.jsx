import { useState, useCallback, useRef, useEffect } from 'react';
import { 
  Upload, Database, Wand2, Download, Copy, Save, 
  Check, AlertCircle, Folder, File, Loader2, RefreshCw, X, GripVertical
} from 'lucide-react';
import { Button, Card, Badge, Alert } from '../components/ui';
import { generateApi } from '../services/api';

const DATABASE_OPTIONS = [
  { id: 'H2', name: 'H2', description: 'In-memory' },
  { id: 'POSTGRESQL', name: 'PostgreSQL', description: 'Production' },
  { id: 'MYSQL', name: 'MySQL', description: 'Popular' },
  { id: 'MONGODB', name: 'MongoDB', description: 'NoSQL' },
  { id: 'SQLITE', name: 'SQLite', description: 'Lightweight' },
];

export default function Generate() {
  const [step, setStep] = useState('upload');
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [selectedDb, setSelectedDb] = useState('H2');
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [currentFile, setCurrentFile] = useState(null);
  const [fileContent, setFileContent] = useState('');
  const [copied, setCopied] = useState(false);
  const [dragOver, setDragOver] = useState(false);
  const [explorerWidth, setExplorerWidth] = useState(25); // percentage
  const [isResizing, setIsResizing] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);
  const containerRef = useRef(null);

  const handleFileSelect = useCallback((file) => {
    if (!file) return;
    
    if (!file.type.startsWith('image/')) {
      setError('Please select an image file');
      return;
    }
    
    if (file.size > 10 * 1024 * 1024) {
      setError('File size must be less than 10MB');
      return;
    }

    setSelectedFile(file);
    setError('');
    
    const reader = new FileReader();
    reader.onload = (e) => setPreviewUrl(e.target.result);
    reader.readAsDataURL(file);
  }, []);

  const handleDrop = useCallback((e) => {
    e.preventDefault();
    setDragOver(false);
    handleFileSelect(e.dataTransfer.files[0]);
  }, [handleFileSelect]);

  const handleGenerate = async () => {
    if (!selectedFile) return;

    setStep('loading');
    setError('');

    try {
      const formData = new FormData();
      formData.append('file', selectedFile);
      formData.append('database', selectedDb);

      const response = await generateApi.analyze(formData);
      
      if (!response.data.success) {
        throw new Error(response.data.message || 'Generation failed');
      }

      setResult(response.data);
      
      const files = response.data.generatedFiles;
      const firstFile = Object.keys(files)[0];
      if (firstFile) {
        setCurrentFile(firstFile);
        setFileContent(files[firstFile]);
      }
      
      setStep('results');
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Generation failed');
      setStep('error');
    }
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(fileContent);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleSave = async () => {
    if (!currentFile || !result?.projectId) return;
    
    try {
      await generateApi.updateFile({
        projectId: result.projectId,
        filePath: currentFile,
        content: fileContent,
      });
      setSaveSuccess(true);
      setTimeout(() => setSaveSuccess(false), 2000);
    } catch (err) {
      console.error('Failed to save:', err);
    }
  };

  const handleMouseDown = (e) => {
    e.preventDefault();
    setIsResizing(true);
  };

  useEffect(() => {
    const handleMouseMove = (e) => {
      if (!isResizing || !containerRef.current) return;
      
      const container = containerRef.current.getBoundingClientRect();
      const newWidth = ((e.clientX - container.left) / container.width) * 100;
      
      if (newWidth >= 15 && newWidth <= 50) {
        setExplorerWidth(newWidth);
      }
    };

    const handleMouseUp = () => {
      setIsResizing(false);
    };

    if (isResizing) {
      document.addEventListener('mousemove', handleMouseMove);
      document.addEventListener('mouseup', handleMouseUp);
    }

    return () => {
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
    };
  }, [isResizing]);

  const handleDownload = () => {
    if (!result?.projectId) return;
    window.location.href = generateApi.downloadProject(result.projectId);
  };

  const handleReset = () => {
    setStep('upload');
    setSelectedFile(null);
    setPreviewUrl(null);
    setResult(null);
    setError('');
    setCurrentFile(null);
    setFileContent('');
  };

  const selectFile = (path) => {
    setCurrentFile(path);
    setFileContent(result.generatedFiles[path]);
  };

  const buildFileTree = (files) => {
    const tree = {};
    Object.keys(files).forEach(path => {
      const parts = path.split('/');
      let current = tree;
      parts.forEach((part, index) => {
        if (index === parts.length - 1) {
          if (!current._files) current._files = [];
          current._files.push({ name: part, path });
        } else {
          if (!current[part]) current[part] = {};
          current = current[part];
        }
      });
    });
    return tree;
  };

  const renderFileTree = (node, prefix = '') => {
    const folders = Object.keys(node).filter(k => k !== '_files').sort();
    const files = node._files || [];

    return (
      <>
        {folders.map(folder => (
          <div key={prefix + folder}>
            <div className="flex items-center gap-2 px-3 py-2 text-sm text-gray-500 dark:text-slate-500">
              <Folder className="w-4 h-4" />
              <span>{folder}</span>
            </div>
            <div className="pl-4">
              {renderFileTree(node[folder], prefix + folder + '/')}
            </div>
          </div>
        ))}
        {files.sort((a, b) => a.name.localeCompare(b.name)).map(file => (
          <button
            key={file.path}
            onClick={() => selectFile(file.path)}
            className={`flex items-center gap-2 w-full px-3 py-2 text-sm text-left rounded-lg transition-colors ${
              currentFile === file.path
                ? 'bg-red-50 dark:bg-red-900/20 text-arche'
                : 'text-gray-600 dark:text-slate-400 hover:bg-gray-100 dark:hover:bg-slate-800'
            }`}
          >
            <File className="w-4 h-4" />
            <span className="truncate">{file.name}</span>
          </button>
        ))}
      </>
    );
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 animate-fade-in">
      {/* Header */}
      <div className="text-center mb-10">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-3">
          Generate Spring Boot Project
        </h1>
        <p className="text-gray-600 dark:text-slate-400">
          Upload your diagram and let AI generate the code
        </p>
      </div>

      {/* Upload Section */}
      {step === 'upload' && (
        <div className="max-w-2xl mx-auto space-y-6">
          {/* Upload Zone */}
          <Card className="p-6">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
              <Upload className="w-5 h-5 text-arche" />
              Upload Diagram
            </h2>
            
            <div
              onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
              onDragLeave={() => setDragOver(false)}
              onDrop={handleDrop}
              onClick={() => document.getElementById('file-input').click()}
              className={`border-2 border-dashed rounded-2xl p-10 text-center cursor-pointer transition-all ${
                dragOver
                  ? 'border-arche bg-red-50 dark:bg-red-950/30'
                  : 'border-gray-200 dark:border-slate-700 hover:border-arche/50 hover:bg-gray-50 dark:hover:bg-slate-800/50'
              }`}
            >
              <input
                id="file-input"
                type="file"
                accept="image/*"
                onChange={(e) => handleFileSelect(e.target.files[0])}
                className="hidden"
              />
              
              {previewUrl ? (
                <div className="space-y-4">
                  <img 
                    src={previewUrl} 
                    alt="Preview" 
                    className="max-h-48 mx-auto rounded-xl shadow-lg"
                  />
                  <p className="text-sm text-gray-600 dark:text-slate-400">
                    {selectedFile?.name}
                  </p>
                  <Button 
                    variant="ghost" 
                    size="sm"
                    onClick={(e) => {
                      e.stopPropagation();
                      setSelectedFile(null);
                      setPreviewUrl(null);
                    }}
                  >
                    <X className="w-4 h-4 mr-2" />
                    Remove
                  </Button>
                </div>
              ) : (
                <>
                  <div className="w-16 h-16 mx-auto mb-4 rounded-2xl bg-red-50 dark:bg-red-950/50 flex items-center justify-center">
                    <Upload className="w-8 h-8 text-arche" />
                  </div>
                  <p className="text-lg font-medium text-gray-900 dark:text-white mb-2">
                    Drop your diagram here
                  </p>
                  <p className="text-sm text-gray-500 dark:text-slate-500">
                    or click to browse • PNG, JPG up to 10MB
                  </p>
                </>
              )}
            </div>
          </Card>

          {/* Database Selection */}
          <Card className="p-6">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
              <Database className="w-5 h-5 text-arche" />
              Select Database
            </h2>
            
            <div className="grid grid-cols-2 sm:grid-cols-5 gap-3">
              {DATABASE_OPTIONS.map((db) => (
                <button
                  key={db.id}
                  onClick={() => setSelectedDb(db.id)}
                  className={`p-4 rounded-xl border-2 text-center transition-all ${
                    selectedDb === db.id
                      ? 'border-arche bg-red-50 dark:bg-red-950/50'
                      : 'border-gray-200 dark:border-slate-700 hover:border-gray-300 dark:hover:border-slate-600'
                  }`}
                >
                  <span className="font-medium text-gray-900 dark:text-white text-sm block">
                    {db.name}
                  </span>
                  <span className="text-xs text-gray-500 dark:text-slate-500">
                    {db.description}
                  </span>
                </button>
              ))}
            </div>
          </Card>

          {error && <Alert variant="error">{error}</Alert>}

          <Button 
            onClick={handleGenerate}
            disabled={!selectedFile}
            className="w-full"
            size="lg"
          >
            <Wand2 className="w-5 h-5 mr-2" />
            Generate Project
          </Button>
        </div>
      )}

      {/* Loading */}
      {step === 'loading' && (
        <Card className="max-w-md mx-auto p-12 text-center">
          <Loader2 className="w-16 h-16 text-arche mx-auto mb-6 animate-spin" />
          <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
            Analyzing with Gemini AI...
          </h2>
          <p className="text-gray-600 dark:text-slate-400">
            Detecting entities and generating code
          </p>
        </Card>
      )}

      {/* Error */}
      {step === 'error' && (
        <Card className="max-w-md mx-auto p-12 text-center">
          <AlertCircle className="w-16 h-16 text-arche mx-auto mb-6" />
          <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
            Generation Failed
          </h2>
          <p className="text-gray-600 dark:text-slate-400 mb-6">{error}</p>
          <Button onClick={handleReset}>
            <RefreshCw className="w-4 h-4 mr-2" />
            Try Again
          </Button>
        </Card>
      )}

      {/* Results */}
      {step === 'results' && result && (
        <div className="space-y-6">
          <Alert variant="success" className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Check className="w-5 h-5" />
              <span>Generated "{result.projectName}" successfully</span>
            </div>
            <Badge variant="primary">{selectedDb}</Badge>
          </Alert>

          {/* Stats */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            {[
              { label: 'Entities', value: result.statistics?.entityCount || 0 },
              { label: 'Repositories', value: result.statistics?.repositoryCount || 0 },
              { label: 'Controllers', value: result.statistics?.controllerCount || 0 },
              { label: 'Total Files', value: Object.keys(result.generatedFiles).length },
            ].map((stat, i) => (
              <Card key={i} className="p-4 text-center bg-arche text-white">
                <div className="text-3xl font-bold">{stat.value}</div>
                <div className="text-sm opacity-90">{stat.label}</div>
              </Card>
            ))}
          </div>

          {/* Code Editor with Resizable Explorer */}
          <Card className="overflow-hidden">
            <div ref={containerRef} className="flex h-[600px] relative">
              {/* File Explorer */}
              <div 
                style={{ width: `${explorerWidth}%` }}
                className="border-r border-gray-200 dark:border-slate-700 overflow-y-auto bg-gray-50 dark:bg-slate-900"
              >
                <div className="p-3 border-b border-gray-200 dark:border-slate-700 sticky top-0 bg-gray-50 dark:bg-slate-900 z-10">
                  <h3 className="text-xs font-semibold text-gray-500 dark:text-slate-400 uppercase">
                    Explorer
                  </h3>
                </div>
                <div className="p-2">
                  {renderFileTree(buildFileTree(result.generatedFiles))}
                </div>
              </div>

              {/* Resize Handle */}
              <div
                onMouseDown={handleMouseDown}
                className={`w-1 bg-gray-200 dark:bg-slate-700 hover:bg-arche dark:hover:bg-arche cursor-col-resize flex items-center justify-center group relative ${
                  isResizing ? 'bg-arche' : ''
                }`}
              >
                <div className="absolute inset-y-0 -left-1 -right-1" />
                <GripVertical className="w-3 h-3 text-gray-400 dark:text-slate-500 opacity-0 group-hover:opacity-100 transition-opacity" />
              </div>

              {/* Code Editor */}
              <div className="flex-1 flex flex-col">
                <div className="flex items-center justify-between p-3 border-b border-gray-200 dark:border-slate-700 bg-gray-50 dark:bg-slate-900">
                  <span className="text-sm font-mono text-gray-600 dark:text-slate-300 truncate mr-4">
                    {currentFile || 'Select a file'}
                  </span>
                  <div className="flex gap-2">
                    <Button variant="ghost" size="sm" onClick={handleSave} disabled={!currentFile}>
                      {saveSuccess ? <Check className="w-4 h-4 text-green-500" /> : <Save className="w-4 h-4" />}
                      <span className="ml-1">{saveSuccess ? 'Saved!' : 'Save'}</span>
                    </Button>
                    <Button variant="ghost" size="sm" onClick={handleCopy}>
                      {copied ? <Check className="w-4 h-4 text-green-500" /> : <Copy className="w-4 h-4" />}
                      <span className="ml-1">{copied ? 'Copied!' : 'Copy'}</span>
                    </Button>
                  </div>
                </div>
                <textarea
                  value={fileContent}
                  onChange={(e) => setFileContent(e.target.value)}
                  className="flex-1 w-full p-4 font-mono text-sm bg-slate-950 dark:bg-black text-gray-100 dark:text-slate-200 resize-none focus:outline-none"
                  placeholder="Select a file to view..."
                  spellCheck={false}
                />
              </div>
            </div>
          </Card>

          {/* Download */}
          <Card className="p-6 bg-arche text-white">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-semibold text-lg">Download Project</h3>
                <p className="text-sm opacity-90">Complete Spring Boot project as ZIP</p>
              </div>
              <Button 
                onClick={handleDownload}
                className="bg-gray-700 text-arche hover:bg-gray-500"
              >
                <Download className="w-4 h-4 mr-2" />
                Download ZIP
              </Button>
            </div>
          </Card>

          <Button variant="secondary" onClick={handleReset} className="w-full">
            <RefreshCw className="w-4 h-4 mr-2" />
            Start Over
          </Button>
        </div>
      )}
    </div>
  );
}
