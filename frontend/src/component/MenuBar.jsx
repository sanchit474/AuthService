import { useNavigate } from "react-router-dom";
import { useContext } from 'react';
import { assets } from "../assets/assets";
import { AppContext } from "../context/AppContext.jsx";

const Menubar = () => {
    const navigate = useNavigate();
    const { isLoggedIn, user, logout } = useContext(AppContext);

    const logoutHandler = async () => {
        await logout();
        navigate('/');
    };

    return (
        <nav className="navbar bg-white px-5 py-4 d-flex justify-content-between align-items-center">
            <div className="d-flex align-items-center gap-2">
                <img src={assets.logo} alt="logo" width={32} height={32} />
                <span className="fw-bold fs-4 text-dark">AuthService</span>
            </div>

            {!isLoggedIn ? (
                <button className="btn btn-outline-dark rounded-pill px-3" onClick={() => navigate('/login')}>
                    Login <i className="bi bi-arrow-right ms-2"></i>
                </button>
            ) : (
                <div className="d-flex align-items-center gap-3">
                    <div className="d-flex align-items-center gap-2">
                        <div className="rounded-circle bg-secondary text-white d-flex align-items-center justify-content-center" style={{ width: 36, height: 36 }}>
                            { (user?.name || user?.email || 'U').charAt(0).toUpperCase() }
                        </div>
                        <span className="fw-semibold">{user?.name || user?.email}</span>
                    </div>
                    <button className="btn btn-outline-danger" onClick={logoutHandler}>Logout</button>
                </div>
            )}
        </nav>
    );
};
export default Menubar; 
