import { useState, useEffect } from 'react';
import { 
  BarChart3, Clock, FolderGit2, Database, 
  Plus, Settings, ChevronRight, Award
} from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Button, Card, Badge, Spinner } from '../components/ui';
import { dashboardApi } from '../services/api';

export default function Dashboard() {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const response = await dashboardApi.getStats();
        setStats(response.data);
      } catch (err) {
        console.error('Failed to fetch stats:', err);
        setError('Failed to load dashboard data');
        // Set default stats on error
        setStats({
          projectsGenerated: 0,
          diagramsAnalyzed: 0,
          avgGenerationTimeMs: 0,
          totalEntities: 0,
        });
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <Spinner size="lg" />
      </div>
    );
  }

  const statCards = [
    { 
      label: 'Projects Generated', 
      value: stats?.projectsGenerated || 0, 
      icon: FolderGit2,
    },
    { 
      label: 'Diagrams Analyzed', 
      value: stats?.diagramsAnalyzed || 0, 
      icon: BarChart3,
    },
    { 
      label: 'Avg Generation Time', 
      value: `${((stats?.avgGenerationTimeMs || 0) / 1000).toFixed(1)}s`, 
      icon: Clock,
    },
    { 
      label: 'Total Entities', 
      value: stats?.totalEntities || 0, 
      icon: Database,
    },
  ];

  const achievements = [
    { label: 'First Project', icon: '🎉', unlocked: (stats?.projectsGenerated || 0) >= 1 },
    { label: '5 Projects', icon: '⭐', unlocked: (stats?.projectsGenerated || 0) >= 5 },
    { label: '10 Projects', icon: '🏆', unlocked: (stats?.projectsGenerated || 0) >= 10 },
    { label: 'Speed Demon', icon: '⚡', unlocked: (stats?.avgGenerationTimeMs || 99999) < 3000 },
  ];

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-12 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-10">
        <div className="flex items-center gap-4">
          <div className="h-16 w-16 rounded-2xl bg-arche flex items-center justify-center text-white text-2xl font-bold shadow-lg">
            {user?.firstName?.[0] || user?.email?.[0]?.toUpperCase() || 'U'}
          </div>
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
              Welcome back, {user?.firstName || 'Developer'}!
            </h1>
            <p className="text-gray-600 dark:text-slate-400">
              Here's your activity overview
            </p>
          </div>
        </div>
        <div className="flex gap-3">
          <Link to="/profile">
            <Button variant="secondary">
              <Settings className="w-4 h-4 mr-2" />
              Settings
            </Button>
          </Link>
          <Link to="/generate">
            <Button>
              <Plus className="w-4 h-4 mr-2" />
              New Project
            </Button>
          </Link>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-10">
        {statCards.map((stat, index) => (
          <Card key={index} hover className="p-6">
            <div className="flex items-center justify-between mb-4">
              <div className="p-3 rounded-xl bg-red-50 dark:bg-red-950/50 text-arche">
                <stat.icon className="w-6 h-6" />
              </div>
            </div>
            <h3 className="text-3xl font-bold text-gray-900 dark:text-white mb-1">
              {stat.value}
            </h3>
            <p className="text-sm text-gray-600 dark:text-slate-400">
              {stat.label}
            </p>
          </Card>
        ))}
      </div>

      {/* Two Column Layout */}
      <div className="grid lg:grid-cols-2 gap-6">
        {/* Quick Actions */}
        <Card className="p-6">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
            Quick Actions
          </h2>
          <div className="space-y-3">
            {[
              { label: 'Generate New Project', icon: Plus, path: '/generate' },
              { label: 'Account Settings', icon: Settings, path: '/profile' },
            ].map((action, index) => (
              <Link
                key={index}
                to={action.path}
                className="flex items-center justify-between p-4 rounded-xl bg-gray-50 dark:bg-slate-800 hover:bg-gray-100 dark:hover:bg-slate-700 transition-colors group"
              >
                <div className="flex items-center gap-3">
                  <div className="p-2 rounded-lg bg-red-50 dark:bg-red-950/50 text-arche">
                    <action.icon className="w-5 h-5" />
                  </div>
                  <span className="font-medium text-gray-900 dark:text-white">
                    {action.label}
                  </span>
                </div>
                <ChevronRight className="w-5 h-5 text-gray-400 group-hover:text-arche transition-colors" />
              </Link>
            ))}
          </div>
        </Card>

        {/* Achievements */}
        <Card className="p-6">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
            <Award className="w-5 h-5 text-arche" />
            Achievements
          </h2>
          <div className="grid grid-cols-2 gap-4">
            {achievements.map((achievement, index) => (
              <div
                key={index}
                className={`p-4 rounded-xl text-center transition-all ${
                  achievement.unlocked
                    ? 'bg-red-50 dark:bg-red-950/50'
                    : 'bg-gray-100 dark:bg-slate-800 opacity-50'
                }`}
              >
                <span className="text-3xl block mb-2">{achievement.icon}</span>
                <span className={`text-sm font-medium ${
                  achievement.unlocked 
                    ? 'text-arche'
                    : 'text-gray-500 dark:text-slate-500'
                }`}>
                  {achievement.label}
                </span>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
}
