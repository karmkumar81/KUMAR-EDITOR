import { useEffect, useState } from "react";

import Preview from "./components/Preview";
import Timeline from "./components/Timeline";

type MediaType = "video" | "photo" | "";

function App() {
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [videoName, setVideoName] = useState("");

  const [mediaURL, setMediaURL] = useState("");
  const [mediaType, setMediaType] = useState<MediaType>("");

  const [isPlaying, setIsPlaying] = useState(false);

  function openFileBrowser() {
    const input = document.getElementById(
      "video-upload"
    ) as HTMLInputElement | null;

    input?.click();
  }

  function handleMediaReady(media: {
    name: string;
    url: string;
    duration: number;
    type: "video" | "photo";
  }) {
    setVideoName(media.name);
    setMediaURL(media.url);
    setMediaType(media.type);

    setDuration(media.duration);
    setCurrentTime(0);

    setIsPlaying(false);
  }

  useEffect(() => {
    if (duration > 0 && currentTime >= duration - 0.05) {
      setIsPlaying(false);
    }
  }, [currentTime, duration]);

  return (
    <div className="app">

      {/* =================================
          TOP TOOLBAR
      ================================= */}

      <header className="kumar-header">

        <div className="kumar-logo">
          🎬
        </div>

        <nav className="editor-menu">

          <button
            className="editor-menu-button active"
            onClick={openFileBrowser}
          >
            <span>📥</span>
            <span>Import Files</span>
          </button>

          <button className="editor-menu-button">
            <span>✂️</span>
            <span>Split</span>
          </button>

          <button className="editor-menu-button">
            <span>🎵</span>
            <span>Audio</span>
          </button>

          <button className="editor-menu-button">
            <span>📝</span>
            <span>Text</span>
          </button>

          <button className="editor-menu-button">
            <span>🖼️</span>
            <span>Media</span>
          </button>

        </nav>

        <div className="kumar-actions">

          <button
            className="kumar-action"
            title="Undo"
          >
            ↶
          </button>

          <button
            className="kumar-action"
            title="Redo"
          >
            ↷
          </button>

          <button
            className="kumar-action"
            title="Settings"
          >
            ⚙
          </button>

        </div>

      </header>


      {/* =================================
          PREVIEW
      ================================= */}

      <div className="editor">

        <Preview
          isPlaying={isPlaying}
          onTimeUpdate={setCurrentTime}
          onDurationChange={setDuration}
          onVideoNameChange={setVideoName}
          onMediaReady={handleMediaReady}
          onPlayPauseComplete={setIsPlaying}
          onPlaybackFinished={() => setIsPlaying(false)}
        />

      </div>


      {/* =================================
          TIMELINE
      ================================= */}

      <Timeline
        currentTime={currentTime}
        duration={duration}
        videoName={videoName}
        mediaURL={mediaURL}
        mediaType={mediaType}
        isPlaying={isPlaying}
        onPlayPause={() => setIsPlaying((playing) => !playing)}
      />

    </div>
  );
}

export default App;