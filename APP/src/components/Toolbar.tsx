import "../styles/Toolbar.css";

function Toolbar() {
  return (
    <aside className="toolbar">

      <div className="toolbar-section">

        <button className="tool-button import-tool">
          <span className="tool-icon">📥</span>
          <span>Import</span>
        </button>

        <button className="tool-button">
          <span className="tool-icon">✂️</span>
          <span>Cut</span>
        </button>

        <button className="tool-button">
          <span className="tool-icon">🎵</span>
          <span>Audio</span>
        </button>

        <button className="tool-button">
          <span className="tool-icon">📝</span>
          <span>Text</span>
        </button>

        <button className="tool-button">
          <span className="tool-icon">🖼️</span>
          <span>Media</span>
        </button>

      </div>

    </aside>
  );
}

export default Toolbar;