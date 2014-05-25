package beta.delux.ghostscanner;

/**
 * Created by Technician on 5/25/14.
 */
public class ThreadUpdate_00 extends Thread {
   /*private boolean running = false;
   private HandlerBluetoothSPP btConn;
   private String tempMSG;

   // private int index;
   // private int[] testing = {90, 90, 110, 180, 90, 90, 70, 00};

   public ThreadUpdate_00(HandlerBluetoothSPP btConn, HandlerBluetooth btHandler) {
      this.btConn = btConn;

      running = true;
      ActivityMain.this.runOnUiThread(new Runnable() {
         public void run() {
            // Button btnConnect = (Button)
            // findViewById(R.id.btnConnect);
            // btnConnect.setText(R.string.button_disconnect);
         }
      });
   }

   public void run() {
      while (running)
         try {
            Thread.sleep(250);
            tempMSG = btConn.getMessage();
            if (!tempMSG.isEmpty()) {
               Log.d(TAG, "RCVD MESSAGE: " + tempMSG);
               decodeMessage(tempMSG);
            }

         } catch (InterruptedException interrupt) {
            Log.e(TAG, "sleep interrupted", interrupt);
            this.cancel();
         } catch (Exception e) {
            Log.e(TAG, "other thread exception", e);
            this.cancel();
         }

   }

   public void decodeMessage(final String inString) {
      ActivityMain.this.runOnUiThread(new Runnable() {
         int tempIndex = -1;
         char tempchar1, tempchar2;

         public void run() {
            try {
               tempIndex = inString.indexOf(0x21);
               if (tempIndex != -1) {
                  tempchar1 = inString.charAt(tempIndex + 1);
                  tempchar2 = inString.charAt(tempIndex + 2);

                  position = Integer.parseInt(Character.toString(tempchar1) + Character.toString(tempchar2)) - 1;
                  if (!OVERRIDE){
                     sbMeter.setProgress(position);
                  }
                  else{
                     if (position != sbMeter.getProgress())
                        sendPacket(getPacket());
                  }
                  ivNeedle.setRotation((position - 11)*6);
               }
            } catch (Exception e) {

            }

         }
      });
   }

   public void cancel() {
      running = false;
      ActivityMain.CONNECTED = false;
      ActivityMain.this.runOnUiThread(new Runnable() {
         public void run() {
            // Button btnConnect = (Button)
            // findViewById(R.id.btnConnect);
            // btnConnect.setText(R.string.button_connect);
         }
      });
      // btConn.stop();
   }*/
}

