import { useState, useContext, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';
import { AppContext } from '../context/AppContext.jsx';

const EmailVerify = () => {
  const { backendURL } = useContext(AppContext);
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState(location?.state?.email || '');
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [resendLoading, setResendLoading] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);

  useEffect(() => {
    let t;
    if (resendCooldown > 0) {
      t = setTimeout(() => setResendCooldown(resendCooldown - 1), 1000);
    }
    return () => clearTimeout(t);
  }, [resendCooldown]);

  const apiBase = backendURL.endsWith('/api') ? backendURL : `${backendURL.replace(/\/$/, '')}/api`;

  const verify = async (e) => {
    e.preventDefault();
    if (!email || !otp) {
      toast.error('Please provide email and OTP');
      return;
    }

    setLoading(true);
    try {
      await axios.post(`${apiBase}/verify-otp`, { email, otp });
      toast.success('Email verified! You can now login.');
      navigate('/login');
    } catch (err) {
      toast.error(err.response?.data || err.message || 'Verification failed');
    } finally {
      setLoading(false);
    }
  };

  const resend = async () => {
    if (!email) {
      toast.error('Please enter your email to resend OTP');
      return;
    }
    setResendLoading(true);
    try {
      await axios.post(`${apiBase}/send-otp`, { email });
      toast.success('OTP resent. Check your email.');
      setResendCooldown(30); // 30s cooldown
    } catch (err) {
      toast.error(err.response?.data || err.message || 'Failed to resend OTP');
    } finally {
      setResendLoading(false);
    }
  };

  return (
    <div 
      className="position-relative min-vh-100 d-flex justify-content-center align-items-center"
      style={{ background: "linear-gradient(90deg, #6a5af9, #8268f9)", border: "none" }}
    >
      <div className="bg-white rounded-4 p-4 shadow-lg" style={{ width: "100%", maxWidth: "420px" }}>
        <h2 className="text-center mb-4">Verify your email</h2>

        <form onSubmit={verify}>
          <div className="mb-3">
            <label className="form-label">Email</label>
            <input
              type="email"
              className="form-control"
              placeholder="name@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="mb-3">
            <label className="form-label">OTP</label>
            <input
              type="text"
              className="form-control"
              placeholder="Enter OTP received in email"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              required
            />
          </div>

          <div className="d-flex gap-2">
            <button className="btn btn-primary flex-grow-1" disabled={loading}>
              {loading ? 'Verifying...' : 'Verify Email'}
            </button>
            <button
              type="button"
              className="btn btn-outline-secondary"
              onClick={resend}
              disabled={resendLoading || resendCooldown > 0}
            >
              {resendLoading ? 'Sending...' : resendCooldown > 0 ? `Resend (${resendCooldown}s)` : 'Resend OTP'}
            </button>
          </div>

          <div className="text-center mt-3">
            <p className="small">Already verified? <button type="button" className="btn btn-link p-0" onClick={() => navigate('/login')} style={{ color: '#6a5af9', fontWeight: 'bold' }}>Sign in</button></p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EmailVerify;