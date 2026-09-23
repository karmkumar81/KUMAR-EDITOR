import { useRef, useState } from "react";

import Header from "./components/header";
import Toolbar from "./components/Toolbar";
import Preview from "./components/Preview";
import Timeline from "./components/Timeline";

function App() {
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [videoName, setVideoName] = useState("");

  const previewRef = useRef<{ openFilePicker: () => void }>(null);

  function handleImport() {
    previewRef.current?.openFilePicker();
  }

  return (
    <div className="app">

      <Header />

      <div className="editor">

        <Toolbar onImport={handleImport} />

        <Preview
          ref={previewRef}
          onTimeUpdate={setCurrentTime}
          onDurationChange={setDuration}
          onVideoNameChange={setVideoName}
        />

      </div>

      <Timeline
        currentTime={currentTime}
        duration={duration}
        videoName={videoName}
      />

    </div>
  );
}

export default App;