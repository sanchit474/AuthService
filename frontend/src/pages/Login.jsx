import { useState, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { AppContext } from '../context/AppContext.jsx';
import { toast } from 'react-toastify';
import { assets } from '../assets/assets'; 

const Login = () => {
    
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();
    const { backendURL, setIsLoggedIn, getUserData } = useContext(AppContext);

    const onSubmitHandler = async (e) => {
        e.preventDefault();
        axios.defaults.withCredentials = true;
        setLoading(true);

        try {
            // Ensure the backend base contains the `/api` context-path used by the server.
            const apiBase = backendURL.endsWith('/api') ? backendURL : `${backendURL.replace(/\/$/, '')}/api`;
            const response = await axios.post(`${apiBase}/login`, { email, password });

            if (response.status === 200 && (response.data?.success ?? true)) {
                setIsLoggedIn(true);
                // Wait for profile to be fetched so UI can immediately reflect the user's name
                await getUserData();
                navigate('/');
                toast.success("Welcome back!");
            } else {
                toast.error(response.data?.message || "Invalid credentials");
            }

        } catch (error) {
            toast.error(error.response?.data?.message || "Invalid email or password");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div 
            className="position-relative min-vh-100 d-flex justify-content-center align-items-center"
            style={{ background: "linear-gradient(90deg, #6a5af9, #8268f9)", border: "none" }}
        >
            {/* --- Top Left Logo --- */}
            <div style={{ position: "absolute", top: "20px", left: "30px", display: "flex", alignItems: "center" }}>
                <Link
                    to="/"
                    style={{
                        display: "flex",
                        gap: 5,
                        alignItems: "center",
                        fontWeight: "bold",
                        fontSize: "24px",
                        textDecoration: "none",
                    }}
                >
                    <img src={assets.logo} alt="logo" height={32} width={32} />
                    <span className="fw-bold fs-4 text-light">AuthService</span>
                </Link>
            </div>

            {/* --- Login Form Card --- */}
            <div className="bg-white rounded-4 p-4 shadow-lg" style={{ width: "100%", maxWidth: "400px" }}>
                <h2 className="text-center mb-4">Login</h2>
                
                {/* Removed the "Please log in to book appointment" paragraph here */}

                <form onSubmit={onSubmitHandler}>
                    
                    {/* 1. Email Input */}
                    <div className="mb-3">
                        <label className="form-label">Email</label>
                        <input 
                            onChange={(e) => setEmail(e.target.value)} 
                            value={email} 
                            type="email" 
                            className="form-control" 
                            required 
                            placeholder="Enter your email"
                        />
                    </div>

                    {/* 2. Password Input */}
                    <div className="mb-3">
                        <label className="form-label">Password</label>
                        <input 
                            onChange={(e) => setPassword(e.target.value)} 
                            value={password} 
                            type="password" 
                            className="form-control" 
                            required 
                            placeholder="Enter your password"
                        />
                    </div>

                    {/* 3. Forgot Password Link */}
                    <div className="mb-3">
                        <Link to="/reset-password" style={{ textDecoration: 'none', color: '#6a5af9', fontSize: '14px' }}>
                            Forgot Password?
                        </Link>
                    </div>

                    <button 
                        className="btn btn-primary w-100" 
                        style={{ backgroundColor: '#6a5af9', borderColor: '#6a5af9' }} 
                        disabled={loading}
                    >
                        {loading ? "Logging in..." : "Login"}
                    </button>

                    {/* 4. Link to Register Page */}
                    <div className="text-center mt-3">
                        <p className="small">
                            Don't have an account?{' '}
                            <Link 
                                to="/register" 
                                style={{ color: '#6a5af9', fontWeight: 'bold', textDecoration: 'none' }}
                            >
                                Create account
                            </Link>
                        </p>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;