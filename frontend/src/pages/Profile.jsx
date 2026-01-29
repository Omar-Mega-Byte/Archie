import { useState, useEffect } from 'react';
import { User, Mail, Lock, Save, Eye, EyeOff, AlertCircle, Calendar } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { Button, Card, Input, Alert, Spinner } from '../components/ui';
import { userApi } from '../services/api';

export default function Profile() {
  const { user } = useAuth();
  
  const [loading, setLoading] = useState(false);
  const [profileData, setProfileData] = useState({
    firstName: '',
    lastName: '',
    email: '',
  });
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [showPasswords, setShowPasswords] = useState({
    current: false,
    new: false,
  });
  const [profileSuccess, setProfileSuccess] = useState('');
  const [profileError, setProfileError] = useState('');
  const [passwordSuccess, setPasswordSuccess] = useState('');
  const [passwordError, setPasswordError] = useState('');

  useEffect(() => {
    if (user) {
      setProfileData({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || '',
      });
    }
  }, [user]);

  const handleProfileChange = (e) => {
    setProfileData({ ...profileData, [e.target.name]: e.target.value });
    setProfileError('');
    setProfileSuccess('');
  };

  const handlePasswordChange = (e) => {
    setPasswordData({ ...passwordData, [e.target.name]: e.target.value });
    setPasswordError('');
    setPasswordSuccess('');
  };

  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setProfileError('');
    setProfileSuccess('');

    try {
      await userApi.updateProfile(profileData);
      setProfileSuccess('Profile updated successfully!');
    } catch (error) {
      setProfileError(error.response?.data?.message || 'Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setPasswordError('');
    setPasswordSuccess('');

    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setPasswordError('New passwords do not match');
      setLoading(false);
      return;
    }

    if (passwordData.newPassword.length < 8) {
      setPasswordError('Password must be at least 8 characters');
      setLoading(false);
      return;
    }

    try {
      await userApi.changePassword({
        currentPassword: passwordData.currentPassword,
        newPassword: passwordData.newPassword,
      });
      
      setPasswordSuccess('Password changed successfully!');
      setPasswordData({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      });
    } catch (error) {
      setPasswordError(error.response?.data?.message || 'Failed to change password');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-12 animate-fade-in">
      {/* Header */}
      <div className="text-center mb-10">
        <div className="h-20 w-20 rounded-2xl bg-arche flex items-center justify-center text-white text-3xl font-bold mx-auto mb-4 shadow-lg">
          {user?.firstName?.[0] || user?.email?.[0]?.toUpperCase() || 'U'}
        </div>
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
          {user?.firstName && user?.lastName 
            ? `${user.firstName} ${user.lastName}`
            : user?.email
          }
        </h1>
        <p className="text-gray-600 dark:text-slate-400 flex items-center justify-center gap-2 mt-2">
          <Calendar className="w-4 h-4" />
          Member since {formatDate(user?.createdAt)}
        </p>
      </div>

      <div className="space-y-6">
        {/* Profile Information */}
        <Card className="p-6">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-6 flex items-center gap-2">
            <User className="w-5 h-5 text-arche" />
            Profile Information
          </h2>

          {profileError && <Alert variant="error" className="mb-4">{profileError}</Alert>}
          {profileSuccess && <Alert variant="success" className="mb-4">{profileSuccess}</Alert>}

          <form onSubmit={handleProfileSubmit} className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Input
                label="First Name"
                name="firstName"
                value={profileData.firstName}
                onChange={handleProfileChange}
                placeholder="John"
              />
              <Input
                label="Last Name"
                name="lastName"
                value={profileData.lastName}
                onChange={handleProfileChange}
                placeholder="Doe"
              />
            </div>
            <Input
              label="Email Address"
              name="email"
              type="email"
              value={profileData.email}
              onChange={handleProfileChange}
              icon={<Mail className="w-5 h-5" />}
              disabled
              className="opacity-60"
            />
            <p className="text-sm text-gray-500 dark:text-slate-500">
              Email cannot be changed
            </p>
            <Button type="submit" loading={loading}>
              <Save className="w-4 h-4 mr-2" />
              Save Changes
            </Button>
          </form>
        </Card>

        {/* Change Password */}
        <Card className="p-6">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-6 flex items-center gap-2">
            <Lock className="w-5 h-5 text-arche" />
            Change Password
          </h2>

          {passwordError && <Alert variant="error" className="mb-4">{passwordError}</Alert>}
          {passwordSuccess && <Alert variant="success" className="mb-4">{passwordSuccess}</Alert>}

          <form onSubmit={handlePasswordSubmit} className="space-y-4">
            <div className="relative">
              <Input
                label="Current Password"
                name="currentPassword"
                type={showPasswords.current ? 'text' : 'password'}
                value={passwordData.currentPassword}
                onChange={handlePasswordChange}
                icon={<Lock className="w-5 h-5" />}
                placeholder="••••••••"
                required
              />
              <button
                type="button"
                onClick={() => setShowPasswords({ ...showPasswords, current: !showPasswords.current })}
                className="absolute right-4 top-10 text-gray-400 hover:text-gray-600"
              >
                {showPasswords.current ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>

            <div className="relative">
              <Input
                label="New Password"
                name="newPassword"
                type={showPasswords.new ? 'text' : 'password'}
                value={passwordData.newPassword}
                onChange={handlePasswordChange}
                icon={<Lock className="w-5 h-5" />}
                placeholder="••••••••"
                required
              />
              <button
                type="button"
                onClick={() => setShowPasswords({ ...showPasswords, new: !showPasswords.new })}
                className="absolute right-4 top-10 text-gray-400 hover:text-gray-600"
              >
                {showPasswords.new ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>

            <Input
              label="Confirm New Password"
              name="confirmPassword"
              type={showPasswords.new ? 'text' : 'password'}
              value={passwordData.confirmPassword}
              onChange={handlePasswordChange}
              icon={<Lock className="w-5 h-5" />}
              placeholder="••••••••"
              required
            />

            <Button type="submit" loading={loading}>
              <Lock className="w-4 h-4 mr-2" />
              Change Password
            </Button>
          </form>
        </Card>
      </div>
    </div>
  );
}
