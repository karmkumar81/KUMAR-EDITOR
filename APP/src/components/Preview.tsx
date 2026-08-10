import { useRef, useState } from "react";
import "../styles/Preview.css";

type PreviewProps = {
  onTimeUpdate: (time: number) => void;
  onDurationChange: (duration: number) => void;
  onVideoNameChange: (name: string) => void;
};

function Preview({
  onTimeUpdate,
  onDurationChange,
  onVideoNameChange,
}: PreviewProps) {

  const videoRef = useRef<HTMLVideoElement>(null);

  const [videoURL, setVideoURL] = useState("");
  const [durationText, setDurationText] = useState("00:00");
  const [resolution, setResolution] = useState("0 × 0");

  function formatTime(seconds: number) {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);

    return `${String(mins).padStart(2, "0")}:${String(secs).padStart(2, "0")}`;
  }

  function handleVideoChange(
    event: React.ChangeEvent<HTMLInputElement>
  ) {
    const file = event.target.files?.[0];

    if (file) {
      onVideoNameChange(file.name);
      setVideoURL(URL.createObjectURL(file));
    }
  }

  function handleLoadedMetadata() {

    if (!videoRef.current) return;

    const d = videoRef.current.duration;

    onDurationChange(d);

    setDurationText(formatTime(d));

    setResolution(
      `${videoRef.current.videoWidth} × ${videoRef.current.videoHeight}`
    );

  }

  function handleTimeUpdate() {

    if (!videoRef.current) return;

    onTimeUpdate(videoRef.current.currentTime);

  }

  return (

    <main className="preview">

      <div className="preview-screen">

        <h2>🎬 Video Preview</h2>

        <input
          type="file"
          accept="video/*"
          onChange={handleVideoChange}
        />

        {videoURL ? (

          <video
            ref={videoRef}
            src={videoURL}
            className="video-player"
            onLoadedMetadata={handleLoadedMetadata}
            onTimeUpdate={handleTimeUpdate}
          />

        ) : (

          <p>No video selected</p>

        )}

        <div className="controls">

          <button onClick={() => videoRef.current?.play()}>▶</button>

          <button onClick={() => videoRef.current?.pause()}>⏸</button>

          <button
            onClick={() => {
              if (videoRef.current) {
                videoRef.current.pause();
                videoRef.current.currentTime = 0;
              }
            }}
          >
            ⏹
          </button>

        </div>

        <div className="video-info">

          <p>Time : 00:00 / {durationText}</p>

          <p>Resolution : {resolution}</p>

        </div>

      </div>

    </main>

  );

}

export default Preview;