[![](https://jitpack.io/v/mathindas/CardFrameCapture.svg)](https://jitpack.io/#mathindas/CardFrameCapture)

# CardFrameCapture Library
![me](https://github.com/mathindas/CardFrameCapture/blob/b58962971b0dd7dc9a99a3e46cda479feb60587f/sample/app_sample.gif)


CardFrameCapture is a Android library that simplifies the process of capturing images within a predefined frame or card. This library provides a user-friendly interface for capturing images, making it ideal for various use cases such as document scanning, ID card capturing, and more.
## Getting Started

To get started with the CardFrameCapture library, follow these simple steps:

### Step 1: Add the Gradle Dependency

First, add the following dependency to your app-level `build.gradle` file:

```gradle
implementation 'com.github.mathindas:cardframecapture:1.0.6'
```

### Step 2: Initialize the Camera

Next, initialize the CardFrameCamera by calling the `create` method and specifying a custom asset (optional) and a custom text prompt:

```kotlin
CardFrameCamera.create(this)
   .openCamera(R.drawable.placeholder, "Take a picture inside the box.")
```

Replace `"R.drawable.placeholder"` with your custom asset and `"Take a picture inside the box."` with your desired text.

This will launch the camera activity with the provided custom asset and text prompt.

### Step 3: Handle the Result

Finally, handle the captured image result in your activity's `onActivityResult` method:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
   super.onActivityResult(requestCode, resultCode, data)

   if (resultCode == CardFrameCamera.RESULT_CODE) {
       val path = CardFrameCamera.getImagePath(data)

       if (!TextUtils.isEmpty(path)) {
           if (requestCode == CardFrameCamera.INTENT_CODE) {
               iv.setImageBitmap(BitmapFactory.decodeFile(path))
           }
       }
   }
}
```

This code retrieves the captured image path from the `Intent` data and loads the image into an `ImageView` using `BitmapFactory.decodeFile`.

## Features

- Capture images within a predefined frame or card
- Images can be cropped flexibly.
- Customizable asset and text prompt
- Easy integration with your Android app
- Efficient and user-friendly interface

## Contributing

Contributions to the CardFrameCapture library are welcome! If you encounter any issues or have suggestions for improvements, please open an issue or submit a pull request on the [GitHub repository](https://github.com/mathindas/CardFrameCapture).

## License

The CardFrameCapture library is released under the [MIT License](https://opensource.org/licenses/MIT).
