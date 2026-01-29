import { forwardRef } from 'react';
import { Loader2 } from 'lucide-react';

// Button Component
export const Button = forwardRef(({ 
  children, 
  variant = 'primary', 
  size = 'md',
  loading = false,
  disabled = false,
  className = '',
  ...props 
}, ref) => {
  const baseStyles = 'inline-flex items-center justify-center font-semibold rounded-xl transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed';
  
  const variants = {
    primary: 'bg-arche hover:bg-arche-dark text-white shadow-lg hover:shadow-xl hover:-translate-y-0.5 active:translate-y-0 focus:ring-red-400',
    secondary: 'bg-gray-100 dark:bg-slate-800 text-gray-700 dark:text-white hover:bg-gray-200 dark:hover:bg-slate-700 border border-gray-200 dark:border-slate-600',
    ghost: 'bg-transparent hover:bg-gray-100 dark:hover:bg-slate-800 text-gray-600 dark:text-slate-300',
    danger: 'bg-red-600 hover:bg-red-700 text-white focus:ring-red-400',
  };

  const sizes = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-5 py-2.5 text-sm',
    lg: 'px-6 py-3 text-base',
  };

  return (
    <button
      ref={ref}
      disabled={disabled || loading}
      className={`${baseStyles} ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    >
      {loading && <Loader2 className="w-4 h-4 mr-2 animate-spin" />}
      {children}
    </button>
  );
});

Button.displayName = 'Button';

// Input Component
export const Input = forwardRef(({ 
  label,
  error,
  icon,
  className = '',
  ...props 
}, ref) => {
  return (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-gray-700 dark:text-slate-300 mb-2">
          {label}
        </label>
      )}
      <div className="relative">
        {icon && (
          <div className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 dark:text-slate-500">
            {icon}
          </div>
        )}
        <input
          ref={ref}
          className={`input ${icon ? 'pl-12' : ''} ${error ? 'border-red-400 focus:ring-red-400' : ''} ${className}`}
          {...props}
        />
      </div>
      {error && (
        <p className="mt-1.5 text-sm text-red-500">{error}</p>
      )}
    </div>
  );
});

Input.displayName = 'Input';

// Card Component
export const Card = forwardRef(({ 
  children,
  hover = false,
  className = '',
  ...props 
}, ref) => {
  return (
    <div
      ref={ref}
      className={`${hover ? 'card-hover' : 'card'} ${className}`}
      {...props}
    >
      {children}
    </div>
  );
});

Card.displayName = 'Card';

// Badge Component
export function Badge({ 
  children, 
  variant = 'default',
  className = '' 
}) {
  const variants = {
    default: 'bg-gray-100 dark:bg-slate-800 text-gray-700 dark:text-slate-200',
    primary: 'bg-red-50 dark:bg-red-950/30 text-arche',
    success: 'bg-green-50 dark:bg-green-950/30 text-green-600 dark:text-green-400',
    warning: 'bg-yellow-50 dark:bg-yellow-950/30 text-yellow-600 dark:text-yellow-400',
    error: 'bg-red-50 dark:bg-red-950/30 text-red-600 dark:text-red-400',
  };

  return (
    <span className={`inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-medium ${variants[variant]} ${className}`}>
      {children}
    </span>
  );
}

// Spinner Component
export function Spinner({ size = 'md', className = '' }) {
  const sizes = {
    sm: 'w-4 h-4',
    md: 'w-8 h-8',
    lg: 'w-12 h-12',
  };

  return (
    <div className={`${sizes[size]} ${className}`}>
      <div className="w-full h-full rounded-full border-4 border-gray-200 dark:border-slate-700 border-t-arche animate-spin" />
    </div>
  );
}

// Alert Component
export function Alert({ 
  children, 
  variant = 'info',
  className = '' 
}) {
  const variants = {
    info: 'bg-blue-50 dark:bg-blue-950/30 text-blue-700 dark:text-blue-400 border-blue-200 dark:border-blue-800',
    success: 'bg-green-50 dark:bg-green-950/30 text-green-700 dark:text-green-400 border-green-200 dark:border-green-800',
    warning: 'bg-yellow-50 dark:bg-yellow-950/30 text-yellow-700 dark:text-yellow-400 border-yellow-200 dark:border-yellow-800',
    error: 'bg-red-50 dark:bg-red-950/30 text-red-700 dark:text-red-400 border-red-200 dark:border-red-800',
  };

  return (
    <div className={`px-4 py-3 rounded-xl border ${variants[variant]} ${className}`}>
      {children}
    </div>
  );
}
