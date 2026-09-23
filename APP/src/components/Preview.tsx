import {
  useEffect,
  useRef,
  useState,
} from "react";

import "../styles/Preview.css";

type MediaReadyData = {
  name: string;
  url: string;
  duration: number;
  type: "video" | "photo";
};

type PreviewProps = {
  isPlaying: boolean;
  onPlayPauseComplete: (playing: boolean) => void;
  onPlaybackFinished: () => void;

  onTimeUpdate: (time: number) => void;
  onDurationChange: (duration: number) => void;
  onVideoNameChange: (name: string) => void;
  onMediaReady: (media: MediaReadyData) => void;
};

function Preview({
  isPlaying,
  onPlayPauseComplete,
  onPlaybackFinished,
  onTimeUpdate,
  onDurationChange,
  onVideoNameChange,
  onMediaReady,
}: PreviewProps) {
  const videoRef =
    useRef<HTMLVideoElement | null>(null);

  const fileInputRef =
    useRef<HTMLInputElement | null>(null);

  const [videoURL, setVideoURL] =
    useState("");

  const [videoName, setVideoName] =
    useState("");

  const [durationText, setDurationText] =
    useState("00:00");

  const [currentTimeText, setCurrentTimeText] =
    useState("00:00");

  const [resolution, setResolution] =
    useState("0 × 0");

  const [videoError, setVideoError] =
    useState("");

  const [isConverting, setIsConverting] =
    useState(false);

  function formatTime(seconds: number) {
    if (!Number.isFinite(seconds)) {
      return "00:00";
    }

    const mins = Math.floor(seconds / 60);

    const secs = Math.floor(seconds % 60);

    return `${String(mins).padStart(2, "0")}:${String(
      secs
    ).padStart(2, "0")}`;
  }

  function openFileBrowser() {
    fileInputRef.current?.click();
  }

  async function handleFileChange(
    event: React.ChangeEvent<HTMLInputElement>
  ) {
    const file = event.target.files?.[0];

    if (!file) {
      return;
    }

    setVideoError("");

    if (file.type.startsWith("video/")) {
      handleVideoFile(file);
    } else if (file.type.startsWith("image/")) {
      await handlePhotoFile(file);
    } else {
      setVideoError(
        "This file type is not supported."
      );
    }

    event.target.value = "";
  }

  function handleVideoFile(file: File) {
    const newURL =
      URL.createObjectURL(file);

    setVideoURL(newURL);
    setVideoName(file.name);

    onVideoNameChange(file.name);

    const temporaryVideo =
      document.createElement("video");

    temporaryVideo.preload = "metadata";
    temporaryVideo.src = newURL;

    temporaryVideo.onloadedmetadata = () => {
      const videoDuration =
        temporaryVideo.duration;

      onDurationChange(videoDuration);

      setDurationText(
        formatTime(videoDuration)
      );

      setResolution(
        `${temporaryVideo.videoWidth} × ${temporaryVideo.videoHeight}`
      );

      onMediaReady({
        name: file.name,
        url: newURL,
        duration: videoDuration,
        type: "video",
      });
    };
  }

  async function handlePhotoFile(
    file: File
  ) {
    try {
      setIsConverting(true);
      setVideoError("");

      const imageURL =
        URL.createObjectURL(file);

      const image = new Image();

      image.src = imageURL;

      await new Promise<void>(
        (resolve, reject) => {
          image.onload = () => resolve();

          image.onerror = () =>
            reject(
              new Error(
                "Could not load image."
              )
            );
        }
      );

      const width =
        image.naturalWidth || 1920;

      const height =
        image.naturalHeight || 1080;

      const canvas =
        document.createElement("canvas");

      canvas.width = width;
      canvas.height = height;

      const context =
        canvas.getContext("2d");

      if (!context) {
        throw new Error(
          "Canvas is not supported."
        );
      }

      const drawingContext = context;

      drawingContext.drawImage(
        image,
        0,
        0,
        width,
        height
      );

      const stream =
        canvas.captureStream(30);

      const mimeType =
        MediaRecorder.isTypeSupported(
          "video/webm;codecs=vp9"
        )
          ? "video/webm;codecs=vp9"
          : "video/webm";

      const recorder =
        new MediaRecorder(stream, {
          mimeType,
        });

      const chunks: Blob[] = [];

      recorder.ondataavailable = (
        event
      ) => {
        if (event.data.size > 0) {
          chunks.push(event.data);
        }
      };

      const recordingFinished =
        new Promise<void>((resolve) => {
          recorder.onstop = () =>
            resolve();
        });

      recorder.start();

      const startTime =
        performance.now();

      function drawFrame() {
        const elapsed =
          performance.now() -
          startTime;

        drawingContext.drawImage(
          image,
          0,
          0,
          width,
          height
        );

        if (elapsed < 5000) {
          requestAnimationFrame(
            drawFrame
          );
        }
      }

      drawFrame();

      await new Promise<void>(
        (resolve) => {
          setTimeout(() => {
            recorder.stop();
            resolve();
          }, 5000);
        }
      );

      await recordingFinished;

      stream
        .getTracks()
        .forEach((track) =>
          track.stop()
        );

      const videoBlob =
        new Blob(chunks, {
          type: "video/webm",
        });

      const videoURL =
        URL.createObjectURL(
          videoBlob
        );

      const convertedName =
        file.name.replace(
          /\.[^/.]+$/,
          ""
        ) + "_5s.webm";

      setVideoURL(videoURL);
      setVideoName(convertedName);

      setDurationText("00:05");

      setResolution(
        `${width} × ${height}`
      );

      onVideoNameChange(
        convertedName
      );

      onDurationChange(5);

      onMediaReady({
        name: convertedName,
        url: videoURL,
        duration: 5,
        type: "photo",
      });

      URL.revokeObjectURL(
        imageURL
      );
    } catch (error) {
      console.error(
        "Photo conversion failed:",
        error
      );

      setVideoError(
        "Could not convert this photo into a video."
      );
    } finally {
      setIsConverting(false);
    }
  }

  function handleLoadedMetadata() {
    if (!videoRef.current) {
      return;
    }

    const video =
      videoRef.current;

    onDurationChange(
      video.duration
    );

    setDurationText(
      formatTime(video.duration)
    );

    setResolution(
      `${video.videoWidth} × ${video.videoHeight}`
    );
  }

  function handleTimeUpdate() {
    if (!videoRef.current) {
      return;
    }

    const time =
      videoRef.current.currentTime;

    onTimeUpdate(time);

    setCurrentTimeText(
      formatTime(time)
    );
  }

  function handleEnded() {
    onPlayPauseComplete(false);
    onPlaybackFinished();
  }

  function handleNativePlay() {
    onPlayPauseComplete(true);
  }

  function handleNativePause() {
    onPlayPauseComplete(false);
  }

  useEffect(() => {
    const video =
      videoRef.current;

    if (!video || !videoURL) {
      return;
    }

    if (isPlaying) {
      video.play().catch(() => {
        onPlayPauseComplete(false);
      });
    } else {
      video.pause();
    }
  }, [
    isPlaying,
    videoURL,
    onPlayPauseComplete,
  ]);

  return (
    <main className="preview">

      <div className="preview-screen">

        <div className="video-area">

          {videoURL ? (
            <video
              key={videoURL}
              ref={videoRef}
              src={videoURL}
              className="video-player"

              /*
               * We remove browser controls because
               * KUMAR now uses the timeline control.
               */
              controls={false}

              preload="metadata"
              playsInline

              onLoadedMetadata={
                handleLoadedMetadata
              }

              onTimeUpdate={
                handleTimeUpdate
              }

              onEnded={
                handleEnded
              }

              onPlay={
                handleNativePlay
              }

              onPause={
                handleNativePause
              }

              onError={() =>
                setVideoError(
                  "This video could not be played by the browser."
                )
              }
            />
          ) : (
            <div className="empty-preview">

              <div className="empty-icon">
                🎬
              </div>

              <h3>
                Start your project
              </h3>

              <p>
                Import a video or photo to begin editing
              </p>

              <div className="empty-actions">

                <button
                  className="empty-media-button primary"
                  onClick={openFileBrowser}
                  disabled={
                    isConverting
                  }
                >
                  <span>
                    🎥
                  </span>

                  <div>
                    <strong>
                      Import Video
                    </strong>

                    <small>
                      MP4, WebM, MOV & more
                    </small>
                  </div>
                </button>

                <button
                  className="empty-media-button"
                  onClick={openFileBrowser}
                  disabled={
                    isConverting
                  }
                >
                  <span>
                    🖼️
                  </span>

                  <div>
                    <strong>
                      Import Photo
                    </strong>

                    <small>
                      Creates a 5-second video
                    </small>
                  </div>
                </button>

              </div>

              <div className="drop-hint">
                Or use{" "}
                <strong>
                  Import Files
                </strong>{" "}
                from the top menu
              </div>

            </div>
          )}

          {isConverting && (
            <div className="conversion-overlay">

              <div className="conversion-spinner"></div>

              <strong>
                Converting photo to video...
              </strong>

              <span>
                Creating a 5-second video
              </span>

            </div>
          )}

          {videoError && (
            <div className="video-error">
              ⚠️ {videoError}
            </div>
          )}

        </div>

        <input
          ref={fileInputRef}
          id="video-upload"
          type="file"
          accept="video/*,image/*"
          onChange={handleFileChange}
          className="hidden-file-input"
        />

        <div className="video-info">

          <span>
            <strong>
              Time
            </strong>

            {currentTimeText} / {durationText}
          </span>

          <span>
            <strong>
              Resolution
            </strong>

            {resolution}
          </span>

          {videoName && (
            <span className="file-info">
              <strong>
                File
              </strong>

              {videoName}
            </span>
          )}

        </div>

      </div>

    </main>
  );
}

export default Preview;