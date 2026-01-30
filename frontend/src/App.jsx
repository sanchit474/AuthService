import { ToastContainer } from 'react-toastify'
import './App.css'
import { Route, Routes } from 'react-router-dom'
import Home from './pages/Home'

import Login from './pages/Login'
import EmailVerify from './pages/EmailVerify'
import ResetPassword from './pages/ResetPassword'
import Menubar from './component/MenuBar'
import Register from './pages/register'

const App = () => {
  return (
    <div>
      <ToastContainer />
      <Routes>
        <Route path='/' element={<Home/>} />
        <Route path='/login' element={<Login/>} />
        <Route path='/register' element={<Register/>} />
        <Route path='/email-verify' element={<EmailVerify/>} />
        <Route path='/reset-password' element={<ResetPassword/>} />
      </Routes>
    </div>
  );
};

export default App;
