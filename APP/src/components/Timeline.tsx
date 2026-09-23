import {
  useEffect,
  useMemo,
  useRef,
} from "react";

import "../styles/Timeline.css";

type MediaType =
  | "video"
  | "photo"
  | "";

type TimelineProps = {
  currentTime: number;
  duration: number;
  videoName: string;
  mediaURL: string;
  mediaType: MediaType;

  isPlaying: boolean;
  onPlayPause: () => void;
};

function Timeline({
  currentTime,
  duration,
  videoName,
  mediaURL,
  mediaType,
  isPlaying,
  onPlayPause,
}: TimelineProps) {
  const viewportRef =
    useRef<HTMLDivElement | null>(
      null
    );

  const pixelsPerSecond = 80;

  const safeDuration =
    Math.max(duration || 1, 1);

  const timelineWidth =
    Math.max(
      900,
      safeDuration *
        pixelsPerSecond +
        100
    );

  const playheadPosition =
    Math.min(
      currentTime *
        pixelsPerSecond,
      timelineWidth - 1
    );

  const rulerMarks = useMemo(() => {
    const marks: number[] = [];

    for (
      let time = 0;
      time <= safeDuration;
      time += 5
    ) {
      marks.push(time);
    }

    if (
      marks.length === 0 ||
      marks[marks.length - 1] <
        safeDuration
    ) {
      marks.push(
        safeDuration
      );
    }

    return marks;
  }, [safeDuration]);

  function formatTime(
    value: number
  ) {
    if (
      !Number.isFinite(value) ||
      value < 0
    ) {
      return "00:00";
    }

    const totalSeconds =
      Math.floor(value);

    const hours =
      Math.floor(
        totalSeconds / 3600
      );

    const minutes =
      Math.floor(
        (totalSeconds % 3600) / 60
      );

    const seconds =
      totalSeconds % 60;

    if (hours > 0) {
      return `${String(hours).padStart(
        2,
        "0"
      )}:${String(minutes).padStart(
        2,
        "0"
      )}:${String(seconds).padStart(
        2,
        "0"
      )}`;
    }

    return `${String(minutes).padStart(
      2,
      "0"
    )}:${String(seconds).padStart(
      2,
      "0"
    )}`;
  }

  useEffect(() => {
    const viewport =
      viewportRef.current;

    if (
      !viewport ||
      duration <= 0
    ) {
      return;
    }

    const visibleStart =
      viewport.scrollLeft;

    const visibleEnd =
      visibleStart +
      viewport.clientWidth;

    if (
      playheadPosition >
      visibleEnd - 100
    ) {
      viewport.scrollTo({
        left:
          playheadPosition -
          viewport.clientWidth *
            0.5,

        behavior: "smooth",
      });
    }
  }, [
    currentTime,
    duration,
    playheadPosition,
  ]);

  return (
    <section className="kumar-timeline">

      <div
        className="timeline-main-area"
        ref={viewportRef}
      >

        <div
          className="timeline-canvas"
          style={{
            width: `${timelineWidth}px`,
          }}
        >

          {/* RULER */}

          <div className="timeline-ruler">

            {rulerMarks.map(
              (time) => (
                <div
                  key={time}
                  className="timeline-ruler-mark"
                  style={{
                    left: `${
                      time *
                      pixelsPerSecond
                    }px`,
                  }}
                >
                  <span>
                    {formatTime(time)}
                  </span>

                  <div className="ruler-tick" />
                </div>
              )
            )}

          </div>


          {/* VIDEO TRACK */}

          <div className="timeline-row">

            <div className="track-label video-track-label">

              <span>
                🎬
              </span>

              <strong>
                Video
              </strong>

            </div>

            <div className="track-content">

              {mediaURL && (
                <div
                  className={
                    mediaType ===
                    "photo"
                      ? "timeline-clip photo-clip"
                      : "timeline-clip"
                  }
                  style={{
                    width:
                      mediaType ===
                      "photo"
                        ? `${
                            5 *
                            pixelsPerSecond
                          }px`
                        : `${
                            Math.max(
                              duration *
                                pixelsPerSecond,
                              120
                            )
                          }px`,
                  }}
                >

                  <span className="clip-icon">
                    {mediaType ===
                    "photo"
                      ? "🖼️"
                      : "🎬"}
                  </span>

                  <span className="clip-name">
                    {videoName}
                  </span>

                  <span className="clip-duration">
                    {formatTime(
                      duration
                    )}
                  </span>

                </div>
              )}

            </div>

          </div>


          {/* AUDIO TRACK */}

          <div className="timeline-row">

            <div className="track-label audio-track-label">

              <span>
                🎵
              </span>

              <strong>
                Audio
              </strong>

            </div>

            <div className="track-content">
              <span className="empty-track">
                Audio track
              </span>
            </div>

          </div>


          {/* TEXT TRACK */}

          <div className="timeline-row">

            <div className="track-label text-track-label">

              <span>
                📝
              </span>

              <strong>
                Text
              </strong>

            </div>

            <div className="track-content">

              <span className="empty-track">
                Text overlays
              </span>

            </div>

          </div>


          {/* PLAYHEAD */}

          {duration > 0 && (
            <div
              className="timeline-playhead"
              style={{
                left: `${playheadPosition}px`,
              }}
            >

              <div className="playhead-handle">
                ▼
              </div>

            </div>
          )}

        </div>

      </div>


      {/* PLAY / PAUSE */}

      <button
        type="button"
        className="timeline-play-button"
        onClick={onPlayPause}
        disabled={!mediaURL}
        title={
          isPlaying
            ? "Pause"
            : "Play"
        }
      >
        {isPlaying
          ? "❚❚"
          : "▶"}
      </button>

    </section>
  );
}

export default Timeline;