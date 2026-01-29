import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useTheme } from '../../contexts/ThemeContext';
import { Sun, Moon, Menu, X, Home, Wand2, LayoutDashboard, User, LogOut, LogIn, UserPlus } from 'lucide-react';
import { useState } from 'react';

export default function Layout({ children }) {
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const location = useLocation();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const navLinks = [
    { to: '/', label: 'Home', icon: Home },
    { to: '/generate', label: 'Generate', icon: Wand2 },
  ];

  const authLinks = user ? [
    { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/profile', label: 'Profile', icon: User },
  ] : [];

  const isActive = (path) => location.pathname === path;

  return (
    <div className="min-h-screen flex flex-col bg-gray-50 dark:bg-slate-950">
      {/* Header */}
      <header className="sticky top-0 z-50 glass border-b border-gray-200 dark:border-slate-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            {/* Logo */}
            <Link to="/" className="flex items-center gap-3 group">
              <img 
                src="/logo.png" 
                alt="ARCHE" 
                className="w-10 h-10 rounded-xl shadow-lg object-cover"
              />
              <div className="hidden sm:block">
                <span className="text-xl font-bold text-gray-900 dark:text-white">ARCHE</span>
                <p className="text-[10px] text-gray-500 dark:text-slate-400 -mt-1">Sketch Once. Code Forever.</p>
              </div>
            </Link>

            {/* Desktop Navigation */}
            <nav className="hidden md:flex items-center gap-1">
              {[...navLinks, ...authLinks].map((link) => (
                <Link
                  key={link.to}
                  to={link.to}
                  className={`flex items-center gap-2 px-4 py-2 rounded-xl font-medium transition-all duration-200 ${
                    isActive(link.to)
                      ? 'bg-red-50 dark:bg-red-900/20 text-arche'
                      : 'text-gray-600 dark:text-slate-400 hover:bg-gray-100 dark:hover:bg-slate-800'
                  }`}
                >
                  <link.icon className="w-4 h-4" />
                  {link.label}
                </Link>
              ))}
            </nav>

            {/* Right Section */}
            <div className="flex items-center gap-2">
              {/* Theme Toggle */}
              <button
                onClick={toggleTheme}
                className="p-2.5 rounded-xl text-gray-600 dark:text-slate-400 hover:bg-gray-100 dark:hover:bg-slate-800 transition-colors"
                aria-label="Toggle theme"
              >
                {theme === 'dark' ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
              </button>

              {/* Auth Buttons */}
              {user ? (
                <div className="hidden md:flex items-center gap-3">
                  <span className="text-sm text-gray-600 dark:text-slate-400">
                    {user.firstName || user.email?.split('@')[0]}
                  </span>
                  <button
                    onClick={handleLogout}
                    className="p-2.5 rounded-xl text-gray-600 dark:text-slate-400 hover:bg-gray-100 dark:hover:bg-slate-800 transition-colors"
                  >
                    <LogOut className="w-5 h-5" />
                  </button>
                </div>
              ) : (
                <div className="hidden md:flex items-center gap-2">
                  <Link to="/login" className="btn-ghost text-sm">
                    Login
                  </Link>
                  <Link to="/register" className="btn-primary text-sm">
                    Get Started
                  </Link>
                </div>
              )}

              {/* Mobile Menu Button */}
              <button
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                className="md:hidden p-2.5 rounded-xl text-gray-600 dark:text-slate-400 hover:bg-gray-100 dark:hover:bg-slate-800"
              >
                {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
              </button>
            </div>
          </div>
        </div>

        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <div className="md:hidden border-t border-gray-200 dark:border-slate-800 bg-white dark:bg-slate-950 animate-fade-in">
            <nav className="px-4 py-4 space-y-1">
              {[...navLinks, ...authLinks].map((link) => (
                <Link
                  key={link.to}
                  to={link.to}
                  onClick={() => setMobileMenuOpen(false)}
                  className={`flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-colors ${
                    isActive(link.to)
                      ? 'bg-red-50 dark:bg-red-900/20 text-arche'
                      : 'text-gray-600 dark:text-slate-400'
                  }`}
                >
                  <link.icon className="w-5 h-5" />
                  {link.label}
                </Link>
              ))}
              
              <div className="pt-4 border-t border-gray-200 dark:border-slate-800">
                {user ? (
                  <button
                    onClick={() => { handleLogout(); setMobileMenuOpen(false); }}
                    className="flex items-center gap-3 w-full px-4 py-3 rounded-xl font-medium text-arche"
                  >
                    <LogOut className="w-5 h-5" />
                    Logout
                  </button>
                ) : (
                  <div className="space-y-2">
                    <Link
                      to="/login"
                      onClick={() => setMobileMenuOpen(false)}
                      className="flex items-center justify-center gap-2 w-full px-4 py-3 rounded-xl font-medium text-gray-600 dark:text-slate-400 border border-gray-200 dark:border-slate-700"
                    >
                      <LogIn className="w-5 h-5" />
                      Login
                    </Link>
                    <Link
                      to="/register"
                      onClick={() => setMobileMenuOpen(false)}
                      className="flex items-center justify-center gap-2 w-full px-4 py-3 rounded-xl font-medium text-white bg-arche"
                    >
                      <UserPlus className="w-5 h-5" />
                      Get Started
                    </Link>
                  </div>
                )}
              </div>
            </nav>
          </div>
        )}
      </header>

      {/* Main Content */}
      <main className="flex-1">
        {children}
      </main>

      {/* Footer - Sticky to bottom */}
      <footer className="mt-auto border-t border-gray-200 dark:border-slate-800 bg-white dark:bg-slate-900">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex flex-col md:flex-row items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-lg bg-arche flex items-center justify-center">
                <span className="text-white font-bold text-sm">A</span>
              </div>
              <div>
                <span className="font-bold text-gray-900 dark:text-white">ARCHE</span>
                <p className="text-xs text-gray-500 dark:text-slate-400">Sketch Once. Code Forever.</p>
              </div>
            </div>
            
            <div className="flex items-center gap-6 text-sm text-gray-500 dark:text-slate-400">
              <a href="#" className="hover:text-arche transition-colors">Documentation</a>
              <a href="#" className="hover:text-arche transition-colors">GitHub</a>
              <a href="#" className="hover:text-arche transition-colors">Support</a>
            </div>
            
            <p className="text-sm text-gray-400 dark:text-slate-500">
              © 2026 Arche. Built with ❤️
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
}
