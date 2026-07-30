import "../styles/EditorLayout.css";

import Navbar from "../components/Navbar";
import Sidebar from "../components/sidebar";
import Preview from "../components/Preview";
import Timeline from "../components/Timeline";
import Statusbar from "../components/Statusbar";

function EditorLayout() {
  return (
    <>
      <Navbar />

      <div className="editor">
        <Sidebar />
        <Preview />
      </div>

      <Timeline />

      <Statusbar />
    </>
  );
}

export default EditorLayout;