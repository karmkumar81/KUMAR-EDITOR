import { useState } from "react";
import "../styles/Timeline.css";

type Clip = {
  id: number;
  name: string;
  start: number;
  duration: number;
};

type TimelineProps = {
  currentTime: number;
  duration: number;
  videoName: string;
};

function Timeline({
  currentTime,
  duration,
  videoName,
}: TimelineProps) {

  const [clips, setClips] = useState<Clip[]>([]);
  const [selectedClip, setSelectedClip] = useState<Clip | null>(null);

  const playheadPosition =
    duration > 0 ? (currentTime / duration) * 100 : 0;

  function addClip() {

    if (!videoName) return;

    const exists = clips.some(
      (clip) => clip.name === videoName
    );

    if (exists) return;

    const newClip = {
      id: Date.now(),
      name: videoName,
      start: 0,
      duration: duration,
    };

    setClips([...clips, newClip]);

  }

  return (

    <section className="timeline">

      <div className="time-ruler">

        <span>00s</span>
        <span>05s</span>
        <span>10s</span>
        <span>15s</span>
        <span>20s</span>

      </div>

      <div
        className="playhead"
        style={{ left: `${playheadPosition}%` }}
      ></div>

      <button
        className="add-button"
        onClick={addClip}
      >
        ➕ Add To Timeline
      </button>

      <div className="track">

        <div className="track-label">
          🎞 Video Track
        </div>

        <div className="track-line">

          {clips.map((clip) => (

            <div
              key={clip.id}
              className={
                selectedClip?.id === clip.id
                  ? "clip selected"
                  : "clip"
              }
              onClick={() => setSelectedClip(clip)}
            >
              🎬 {clip.name}
            </div>

          ))}

        </div>

      </div>

      <div className="track">

        <div className="track-label">
          🎵 Audio Track
        </div>

        <div className="track-line"></div>

      </div>

      <div className="track">

        <div className="track-label">
          📝 Text Track
        </div>

        <div className="track-line"></div>

      </div>

      <div className="clip-properties">

        <h3>📋 Clip Properties</h3>

        {selectedClip ? (

          <>
            <p><strong>Name:</strong> {selectedClip.name}</p>
            <p><strong>Start:</strong> {selectedClip.start} sec</p>
            <p><strong>Duration:</strong> {selectedClip.duration.toFixed(2)} sec</p>
          </>

        ) : (

          <p>No clip selected</p>

        )}

      </div>

    </section>

  );

}

export default Timeline;