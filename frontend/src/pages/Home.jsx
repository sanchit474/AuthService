import Menubar from "../component/MenuBar";
import Header from "../component/Header";
const Home = () => {
    return (
        <div className="flex flex-column items-center justify-content-center min-vh-100">
            <Menubar />
            <Header />
        </div>
    );
};

export default Home;