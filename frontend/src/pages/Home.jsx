import { Link } from 'react-router-dom';
import { Wand2, Upload, Database, Download, ArrowRight, Sparkles, Zap, Shield } from 'lucide-react';
import { Button, Card } from '../components/ui';
import { useAuth } from '../contexts/AuthContext';

export default function Home() {
  const { user } = useAuth();

  const features = [
    {
      icon: Upload,
      title: 'Upload Diagram',
      description: 'Simply upload your ER diagram or database schema image',
    },
    {
      icon: Sparkles,
      title: 'AI Analysis',
      description: 'Gemini AI analyzes and extracts entities, relationships',
    },
    {
      icon: Zap,
      title: 'Code Generation',
      description: 'Get complete Spring Boot project with all layers',
    },
    {
      icon: Download,
      title: 'Download & Run',
      description: 'Download ready-to-run project with one click',
    },
  ];

  const stats = [
    { value: '10K+', label: 'Projects Generated' },
    { value: '5', label: 'Database Types' },
    { value: '99%', label: 'Accuracy Rate' },
    { value: '<9s', label: 'Avg Generation' },
  ];

  return (
    <div className="animate-fade-in">
      {/* Hero Section */}
      <section className="relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-red-50 to-white dark:from-slate-900 dark:to-slate-950" />
        <div className="absolute top-20 left-10 w-72 h-72 bg-arche/10 rounded-full blur-3xl" />
        <div className="absolute bottom-20 right-10 w-96 h-96 bg-arche/5 rounded-full blur-3xl" />
        
        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 lg:py-32">
          <div className="text-center max-w-4xl mx-auto">
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-red-50 dark:bg-red-950/50 text-arche text-sm font-medium mb-8">
              <Sparkles className="w-4 h-4" />
              Powered by Gemini AI
            </div>
            
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold text-gray-900 dark:text-white mb-6 leading-tight">
              Sketch Once.<br />
              <span className="text-arche">Code Forever.</span>
            </h1>
            
            <p className="text-lg sm:text-xl text-gray-600 dark:text-slate-400 mb-10 max-w-2xl mx-auto">
              Transform your database diagrams into production-ready Spring Boot applications instantly with AI-powered code generation.
            </p>
            
            <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
              <Link to="/generate">
                <Button size="lg" className="w-full sm:w-auto">
                  <Wand2 className="w-5 h-5 mr-2" />
                  Start Generating
                </Button>
              </Link>
              {!user && (
                <Link to="/register">
                  <Button variant="secondary" size="lg" className="w-full sm:w-auto">
                    Create Account
                    <ArrowRight className="w-5 h-5 ml-2" />
                  </Button>
                </Link>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-16 bg-white dark:bg-slate-900 border-y border-gray-100 dark:border-slate-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-8">
            {stats.map((stat, index) => (
              <div key={index} className="text-center">
                <div className="text-3xl sm:text-4xl font-bold text-arche mb-2">
                  {stat.value}
                </div>
                <div className="text-sm text-gray-600 dark:text-slate-400">
                  {stat.label}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section className="py-20 lg:py-28">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl sm:text-4xl font-bold text-gray-900 dark:text-white mb-4">
              How It Works
            </h2>
            <p className="text-lg text-gray-600 dark:text-slate-400 max-w-2xl mx-auto">
              From diagram to deployment in four simple steps
            </p>
          </div>
          
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {features.map((feature, index) => (
              <Card key={index} hover className="p-6 text-center group">
                <div className="w-14 h-14 mx-auto mb-5 rounded-2xl bg-red-50 dark:bg-red-950/50 flex items-center justify-center text-arche group-hover:scale-110 transition-transform">
                  <feature.icon className="w-7 h-7" />
                </div>
                <div className="text-xs font-semibold text-arche mb-2">
                  STEP {index + 1}
                </div>
                <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                  {feature.title}
                </h3>
                <p className="text-sm text-gray-600 dark:text-slate-400">
                  {feature.description}
                </p>
              </Card>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
