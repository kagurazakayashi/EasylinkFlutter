package moe.yashi.easylink_flutter;

import io.flutter.plugin.common.BinaryMessenger;
import android.content.Context;
import io.flutter.plugin.common.EventChannel;
import android.net.ConnectivityManager;
import android.util.Log;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** EasylinkFlutterPlugin */
public class EasylinkFlutterPlugin implements FlutterPlugin {
    private static final String TAG = "EasylinkFlutterPlugin";

    private MethodChannel methodChannel;
    private EventChannel eventChannel;

    public static void registerWith(Registrar registrar) {
        EasylinkFlutterPlugin plugin = new EasylinkFlutterPlugin();
        plugin.setupChannels(registrar.messenger(), registrar.context());
    }

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        setupChannels(binding.getFlutterEngine().getDartExecutor(), binding.getApplicationContext());
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