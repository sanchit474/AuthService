import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';
import { AppContext } from '../context/AppContext.jsx';
import { assets } from '../assets/assets';

const Register = () => {
  const navigate = useNavigate();
  const { backendURL } = useContext(AppContext);

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const onSubmit = async (e) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      toast.error('Passwords do not match');
      return;
    }

    setLoading(true);
    try {
      const apiBase = backendURL.endsWith('/api') ? backendURL : `${backendURL.replace(/\/$/, '')}/api`;
      const res = await axios.post(`${apiBase}/register`, { name, email, password });

      toast.success('Registration successful! Please verify your email.');
      // Navigate to email verify page and pass the email so verification can be pre-filled
      navigate('/email-verify', { state: { email } });
    } catch (err) {
      toast.error(err.response?.data || err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div 
      className="position-relative min-vh-100 d-flex justify-content-center align-items-center"
      style={{ background: "linear-gradient(90deg, #6a5af9, #8268f9)", border: "none" }}
    >
      <div style={{ position: "absolute", top: "20px", left: "30px", display: "flex", alignItems: "center" }}>
        <button className="btn btn-link text-light p-0" onClick={() => navigate('/') }>
          <img src={assets.logo} alt="logo" height={32} width={32} />
          
        </button>
      </div>

      <div className="bg-white rounded-4 p-4 shadow-lg" style={{ width: "100%", maxWidth: "480px" }}>
        <h2 className="text-center mb-4">Create Account</h2>
        <form onSubmit={onSubmit}>
          <div className="mb-3">
            <label className="form-label">Full name</label>
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              type="text"
              className="form-control"
              placeholder="Your full name"
              required
            />
          </div>

          <div className="mb-3">
            <label className="form-label">Email</label>
            <input
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              type="email"
              className="form-control"
              placeholder="name@domain.com"
              required
            />
          </div>

          <div className="mb-3">
            <label className="form-label">Password</label>
            <input
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              type="password"
              className="form-control"
              placeholder="At least 6 characters"
              required
              minLength={6}
            />
          </div>

          <div className="mb-3">
            <label className="form-label">Confirm Password</label>
            <input
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              type="password"
              className="form-control"
              placeholder="Re-enter password"
              required
              minLength={6}
            />
          </div>

          <button className="btn btn-primary w-100" disabled={loading} style={{ backgroundColor: '#6a5af9', borderColor: '#6a5af9' }}>
            {loading ? 'Creating account...' : 'Create account'}
          </button>

          <div className="text-center mt-3">
            <p className="small">Already have an account? <button type="button" className="btn btn-link p-0" onClick={() => navigate('/login')} style={{ color: '#6a5af9', fontWeight: 'bold' }}>Sign in</button></p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Register;