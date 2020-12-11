# iiVisu
[![](https://jitpack.io/v/ImnIrdst/iiVisu.svg)](https://jitpack.io/#ImnIrdst/iiVisu)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-iiVisu-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/8186)

A player/ recorder visualizer with the swipe to seek functionality.

# Demo

![](https://github.com/ImnIrdst/iiVisu/blob/main/demo/iivisu-record.gif)
![](https://github.com/ImnIrdst/iiVisu/blob/main/demo/iivisu-play.gif)

# Setup

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
Step 2. Add the dependency
```
dependencies {
  implementation 'com.github.imnirdst:iivisu:1.1.0'
}
```

# Usage

This repository contains a sample app that shows how to use iiVisu.

## Recorder

[`RecorderVisualizer`](https://github.com/ImnIrdst/iiVisu/blob/main/iivisu/src/main/java/com/imn/iivisu/RecorderVisualizer.kt) doesn't support swipe to seek functionality.

```XML
<com.imn.iivisu.RecorderVisualizer
    android:id="@+id/visualizer"
    android:layout_width="0dp"
    android:layout_height="256dp"
    app:maxAmp="100"
    app:barWidth="3dp"
    app:spaceBetweenBar="2dp"
    app:loadedBarPrimeColor="@color/primaryLight"
    app:backgroundBarPrimeColor="@color/gray"
    app:layout_constraintBottom_toTopOf="@id/timeline_text_view"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent" />
```

```Kotlin
visualizer.ampNormalizer = { sqrt(it.toFloat()).toInt() }

recorder.apply {
    onStop = { visualizer.clear() }
    onAmpListener = {
        runOnUiThread {
            visualizer.addAmp(it)
        }
    }
}
```

## Player
```XML
<com.imn.iivisu.PlayerVisualizer
    android:id="@+id/visualizer"
    android:layout_width="0dp"
    android:layout_height="256dp"
    app:barWidth="3dp"
    app:spaceBetweenBar="2dp"
    app:approximateBarDuration="50"
    app:loadedBarPrimeColor="@color/primaryLight"
    app:backgroundBarPrimeColor="@color/gray"
    app:layout_constraintBottom_toTopOf="@id/timeline_text_view"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent" />
```

```Kotlin
visualizer.apply {
    onStartSeeking = { player.pause() }
    onSeeking = { timelineTextView.text = it.formatAsTime() }
    onFinishedSeeking = { time, isPlayingBefore ->
        player.seekTo(time)
        if (isPlayingBefore) {
            player.resume()
        }
    onAnimateToPositionFinished = { time, isPlaying ->
            player.seekTo(time)
        }
    }
}

visualizer.ampNormalizer = { sqrt(it.toFloat()).toInt() }

player.onProgress = { time, isPlaying ->
    visualizer.updateTime(time.toInt(), isPlaying)
}
```

# Documentation
- `maxAmp`: Maximum amp that you expected to receive from the mic. Amps with higher than maxAmp are shown as a full height bar. This is calculated automatically in the `PlayerVisualizer`.

- `barWidth`: Width of each bar.

- `spaceBetweenBar`: Space between each bar.

- `approximateBarDuration`: Defines approximate duration of each bar. The exact duration of each bar is calculated and stored in the `barDuration` variable.

- `loadedBarPrimeColor`: Defines loaded bar color.

- `backgroundBarPrimeColor`: Defines background (unloaded) bar color.

- `ampNormalizer`: Receives a lambda method for normalizing amps. (for better visualization)

- `addAmp`: Used for adding an amp to `RecorderVisualizer` and its bar gets drawn Immediately.

- `updateTime`: Used for updating `PlayerVisualizer` timeline to specified location. `isPlaying` param is used for defining behaviour of `onFinishedSeeking` callback.

- `onStartSeeking` : Receives a callback for the action needed to happen when seeking starts.

- `onSeeking` : Receives a callback for the action needed to happen during the seeking process and contains current time position of the visualizer.

- `onFinishedSeeking`: Receives a callback for the action needed to happen after the seeking finishes and contains time position of the visualizer and a variable for deciding whether you need to resume player after seeking or not.

- `seekOver(amount)`: Moves visualizer's cursor `amount` ahead/back and notifies using `onAnimateToPositionFinished` callback.

- `seekTo(position)`: Moves visualizer's cursor to `position` and notifies using `onAnimateToPositionFinished` callback.

- `onAnimateToPositionFinished`: Receives a callback for the action needed to happen after the moving to position finishes and contains time position of the visualizer and a variable for deciding whether you need to resume player after seeking or not.

## License
MIT. See the [LICENSE](https://github.com/ImnIrdst/iiVisu/blob/main/LICENSE) file for details.
