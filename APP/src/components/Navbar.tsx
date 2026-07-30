import "../styles/Navbar.css";

function Navbar() {
  return (
    <header className="navbar">
      <div className="logo">
        🎬 <span>KUMAR</span>
      </div>

      <nav className="menu">
        <button>File</button>
        <button>Edit</button>
        <button>View</button>
        <button>Export</button>
      </nav>

      <div className="actions">
        <button>Settings</button>
      </div>
    </header>
  );
}

export default Navbar;