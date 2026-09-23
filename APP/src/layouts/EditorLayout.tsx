
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
        <Preview
          onTimeUpdate={() => {}}
          onDurationChange={() => {}}
          onVideoNameChange={() => {}}
        />
      </div>

      <Timeline currentTime={0} duration={0} videoName="" />

      <Statusbar />
    </>
  );
}

export default EditorLayout;