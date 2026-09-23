import "../styles/Header.css";

type HeaderProps = {
  onImport: () => void;
};

function Header({ onImport }: HeaderProps) {
  return (
    <header className="header">
      <div className="header-top">
        <div className="brand">
          <div className="brand-icon">🎬</div>

          <div className="brand-text">
            <span className="brand-name">KUMAR</span>
            <span className="brand-subtitle">VIDEO EDITOR</span>
          </div>
        </div>

        <div className="header-actions">
          <button className="icon-button" title="Undo">
            ↶
          </button>

          <button className="icon-button" title="Redo">
            ↷
          </button>

          <button className="settings-button" title="Settings">
            ⚙
          </button>
        </div>
      </div>

      <nav className="main-menu" aria-label="Main menu">
        <button
          className="menu-button active"
          onClick={onImport}
          title="Import video or photo"
        >
          <span className="menu-icon">📥</span>
          <span>Import Files</span>
        </button>

        <button className="menu-button" title="Split selected clip">
          <span className="menu-icon">✂️</span>
          <span>Split</span>
        </button>

        <button className="menu-button" title="Add or edit audio">
          <span className="menu-icon">🎵</span>
          <span>Audio</span>
        </button>

        <button className="menu-button" title="Add text">
          <span className="menu-icon text-icon">T</span>
          <span>Text</span>
        </button>

        <button className="menu-button" title="Add media and overlays">
          <span className="menu-icon">🖼️</span>
          <span>Media</span>
        </button>
      </nav>
    </header>
  );
}

export default Header;