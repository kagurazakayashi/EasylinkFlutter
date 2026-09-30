package moe.yashi.easylink_flutter;

import android.content.Context;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

/** EasylinkFlutterPlugin */
public class EasylinkFlutterPlugin implements FlutterPlugin {
    private static final String TAG = "EasylinkFlutterPlugin";

    private MethodChannel methodChannel;
    private EventChannel eventChannel;

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        setupChannels(binding.getBinaryMessenger(), binding.getApplicationContext());
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        teardownChannels();
    }

    private void setupChannels(BinaryMessenger messenger, Context context) {
        methodChannel = new MethodChannel(messenger, "easylink_flutter");
        eventChannel = new EventChannel(messenger, "easylink_flutter");
        EasylinkMethodChannelHandler methodChannelHandler = new EasylinkMethodChannelHandler(messenger, context, methodChannel);

        methodChannel.setMethodCallHandler(methodChannelHandler);
    }

    private void teardownChannels() {
        methodChannel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);
        methodChannel = null;
        eventChannel = null;
    }
}