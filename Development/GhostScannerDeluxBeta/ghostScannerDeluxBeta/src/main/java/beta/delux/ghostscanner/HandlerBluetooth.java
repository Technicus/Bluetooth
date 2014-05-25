package beta.delux.ghostscanner;

/**
 * Created by Technician on 5/25/14.
 */
import android.os.Handler;
import android.os.Message;

public class HandlerBluetooth extends Handler {

   public enum MessageType{
      STATE,
      READ,
      WRITE,
      DEVICE,
      NOTIFY;
   }

   public Message obtainMessage(MessageType message, int count, Object obj){
      return obtainMessage(message.ordinal(), count, -1, obj);
   }

   public MessageType getMessageType(int ordinal){
      return MessageType.values()[ordinal];
   }

}
