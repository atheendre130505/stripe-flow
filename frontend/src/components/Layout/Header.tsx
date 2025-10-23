import React, { useState } from 'react';
import { 
  Menu, 
  Bell, 
  Search, 
  User, 
  Settings,
  LogOut,
  ChevronDown
} from 'lucide-react';
import { cn } from '../../utils';

interface HeaderProps {
  onMenuClick: () => void;
}

const Header: React.FC<HeaderProps> = ({ onMenuClick }) => {
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);

  return (
    <header className="bg-white shadow-sm border-b border-secondary-200">
      <div className="flex items-center justify-between h-16 px-6">
        {/* Left side */}
        <div className="flex items-center space-x-4">
          <button
            onClick={onMenuClick}
            className="p-2 rounded-md text-secondary-600 hover:text-secondary-900 hover:bg-secondary-100 lg:hidden"
          >
            <Menu className="w-5 h-5" />
          </button>
          
          {/* Search */}
          <div className="hidden md:block">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-secondary-400" />
              <input
                type="text"
                placeholder="Search transactions, customers..."
                className="pl-10 pr-4 py-2 w-80 border border-secondary-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
            </div>
          </div>
        </div>

        {/* Right side */}
        <div className="flex items-center space-x-4">
          {/* Notifications */}
          <div className="relative">
            <button
              onClick={() => setShowNotifications(!showNotifications)}
              className="p-2 rounded-md text-secondary-600 hover:text-secondary-900 hover:bg-secondary-100 relative"
            >
              <Bell className="w-5 h-5" />
              <span className="absolute -top-1 -right-1 w-3 h-3 bg-error-500 rounded-full"></span>
            </button>
            
            {showNotifications && (
              <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg border border-secondary-200 z-50">
                <div className="p-4 border-b border-secondary-200">
                  <h3 className="text-lg font-semibold text-secondary-900">Notifications</h3>
                </div>
                <div className="max-h-64 overflow-y-auto">
                  <div className="p-4 border-b border-secondary-100">
                    <div className="text-sm text-secondary-900">Payment received</div>
                    <div className="text-xs text-secondary-500">$50.00 from John Doe</div>
                    <div className="text-xs text-secondary-400">2 minutes ago</div>
                  </div>
                  <div className="p-4 border-b border-secondary-100">
                    <div className="text-sm text-secondary-900">Webhook failed</div>
                    <div className="text-xs text-secondary-500">Endpoint: https://example.com/webhook</div>
                    <div className="text-xs text-secondary-400">5 minutes ago</div>
                  </div>
                </div>
                <div className="p-4 border-t border-secondary-200">
                  <button className="text-sm text-primary-600 hover:text-primary-700">
                    View all notifications
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* User menu */}
          <div className="relative">
            <button
              onClick={() => setShowUserMenu(!showUserMenu)}
              className="flex items-center space-x-2 p-2 rounded-lg hover:bg-secondary-100"
            >
              <div className="w-8 h-8 bg-primary-600 rounded-full flex items-center justify-center">
                <User className="w-4 h-4 text-white" />
              </div>
              <div className="hidden md:block text-left">
                <div className="text-sm font-medium text-secondary-900">Admin User</div>
                <div className="text-xs text-secondary-500">admin@stripeflow.com</div>
              </div>
              <ChevronDown className="w-4 h-4 text-secondary-400" />
            </button>

            {showUserMenu && (
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-secondary-200 z-50">
                <div className="py-1">
                  <button className="flex items-center w-full px-4 py-2 text-sm text-secondary-700 hover:bg-secondary-100">
                    <User className="w-4 h-4 mr-3" />
                    Profile
                  </button>
                  <button className="flex items-center w-full px-4 py-2 text-sm text-secondary-700 hover:bg-secondary-100">
                    <Settings className="w-4 h-4 mr-3" />
                    Settings
                  </button>
                  <hr className="my-1" />
                  <button className="flex items-center w-full px-4 py-2 text-sm text-error-600 hover:bg-error-50">
                    <LogOut className="w-4 h-4 mr-3" />
                    Sign out
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
