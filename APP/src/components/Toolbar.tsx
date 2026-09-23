import "../styles/Toolbar.css";

type ToolbarProps = {
  onImport: () => void;
};

function Toolbar({ onImport }: ToolbarProps) {
  return (
    <aside className="toolbar">

      <div className="toolbar-section">

        <button
          className="tool-button import-tool"
          onClick={onImport}
        >
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