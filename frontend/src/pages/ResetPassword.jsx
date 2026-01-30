import { useState, useContext } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';
import { AppContext } from '../context/AppContext.jsx';

const ResetPassword = () => {
  const { backendURL } = useContext(AppContext);
  const navigate = useNavigate();
  const location = useLocation();

  const [step, setStep] = useState('request'); // 'request' or 'reset'
  const [email, setEmail] = useState(location?.state?.email || '');
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [requesting, setRequesting] = useState(false);

  const apiBase = backendURL.endsWith('/api') ? backendURL : `${backendURL.replace(/\/$/, '')}/api`;

  const requestOtp = async (e) => {
    e?.preventDefault();
    if (!email) return toast.error('Please enter your email');
    setRequesting(true);
    try {
      await axios.post(`${apiBase}/send-reset-otp`, { email });
      toast.success('If an account exists, an OTP was sent to your email.');
      setStep('reset');
    } catch (err) {
      toast.error(err.response?.data || err.message || 'Failed to send OTP');
    } finally {
      setRequesting(false);
    }
  };

  const reset = async (e) => {
    e.preventDefault();
    if (!email || !otp || !newPassword || !confirmPassword) return toast.error('All fields are required');
    if (newPassword.length < 6) return toast.error('Password must be at least 6 characters');
    if (newPassword !== confirmPassword) return toast.error('Passwords do not match');

    setLoading(true);
    try {
      await axios.post(`${apiBase}/reset-password`, { email, otp, newPassword });
      toast.success('Password successfully reset. Please login with your new password.');
      navigate('/login');
    } catch (err) {
      toast.error(err.response?.data || err.message || 'Failed to reset password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="position-relative min-vh-100 d-flex justify-content-center align-items-center" style={{ background: "linear-gradient(90deg, #6a5af9, #8268f9)", border: "none" }}>
      <div className="bg-white rounded-4 p-4 shadow-lg" style={{ width: "100%", maxWidth: "480px" }}>
        {step === 'request' ? (
          <form onSubmit={requestOtp}>
            <h2 className="text-center mb-4">Forgot password</h2>

            <div className="mb-3">
              <label className="form-label">Email</label>
              <input type="email" className="form-control" placeholder="name@example.com" value={email} onChange={(e) => setEmail(e.target.value)} required />
            </div>

            <button className="btn btn-primary w-100" disabled={requesting}>{requesting ? 'Sending OTP...' : 'Send OTP'}</button>

            <div className="text-center mt-3">
              <p className="small">Remembered your password? <button type="button" className="btn btn-link p-0" onClick={() => navigate('/login')} style={{ color: '#6a5af9', fontWeight: 'bold' }}>Sign in</button></p>
            </div>
          </form>
        ) : (
          <form onSubmit={reset}>
            <h2 className="text-center mb-4">Reset password</h2>

            <div className="mb-3">
              <label className="form-label">Email</label>
              <input type="email" className="form-control" placeholder="name@example.com" value={email} onChange={(e) => setEmail(e.target.value)} required />
            </div>

            <div className="mb-3">
              <label className="form-label">OTP</label>
              <input type="text" className="form-control" placeholder="Enter OTP" value={otp} onChange={(e) => setOtp(e.target.value)} required />
            </div>

            <div className="mb-3">
              <label className="form-label">New password</label>
              <input type="password" className="form-control" placeholder="New password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} minLength={6} required />
            </div>

            <div className="mb-3">
              <label className="form-label">Confirm password</label>
              <input type="password" className="form-control" placeholder="Confirm password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} minLength={6} required />
            </div>

            <div className="d-flex gap-2">
              <button className="btn btn-primary flex-grow-1" disabled={loading}>{loading ? 'Resetting...' : 'Reset password'}</button>
              <button type="button" className="btn btn-outline-secondary" onClick={() => setStep('request')}>Back</button>
            </div>

            <div className="text-center mt-3">
              <p className="small">Already reset? <button type="button" className="btn btn-link p-0" onClick={() => navigate('/login')} style={{ color: '#6a5af9', fontWeight: 'bold' }}>Sign in</button></p>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default ResetPassword;