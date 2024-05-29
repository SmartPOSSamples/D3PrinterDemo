package com.cloudpos.d3printer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.cashdrawer.CashDrawerDevice;
import com.cloudpos.d3printerdemo.R;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.cloudpos.sdk.printer.html.PrinterHtmlListener;
import com.orhanobut.logger.Logger;
import com.cloudpos.d3printer.util.PrintTagForQ1;
import com.cloudpos.d3printer.util.PrinterCommand;
import com.cloudpos.d3printer.util.PurchaseBillForQ1Entity;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    PrinterDevice device = null;

    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_land);
        setTitle("" + new Date());
        mContext = this;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();

        }
    };

    public void initPrinter(View v) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                device = (PrinterDevice) POSTerminal.getInstance(mContext).getDevice("cloudpos.device.printer");
                try {
                    device.open();
                    handler.obtainMessage(1, "printer already opened").sendToTarget();
                } catch (Exception e) {
                    handler.obtainMessage(1, "printer open exception  ...").sendToTarget();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void printerPrint(View v) {
        try {
            if (device == null) {
                Toast.makeText(MainActivity.this, "please open printer", Toast.LENGTH_SHORT).show();
                return;
            }
            device.printText(device.getDefaultParameters(), " -------------------------------------------------------" +
                    "In the spring of 2012, a group of payment industry veterans from payment, mobile communication,\n" +
                    "and security industries got together. Their expertise and accomplishments in the past 20+ years are\n\n\n\n\n\n");
            device.printText("\n");
            device.printText("\n");
            device.printText("\n");
            device.printText("In the spring of 2012, a group of payment industry veterans from payment, mobile communication,\n" +
                    "and security industries got together. Their expertise and accomplishments in the past 20+ years are。++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n\n\n\n\n");
            device.printText("\n");
            device.printText("\n");
            device.printText("\n");
            device.cutPaper();
        } catch (DeviceException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void printImg(View v) {
        try {
            if (device == null) {
                Toast.makeText(MainActivity.this, "please open printer", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeStream(mContext.getResources().getAssets().open("weixinxxxx.png"));
            device.printBitmap(bitmap);
            bitmap.recycle();
            device.printText("\n");
            device.printText("\n");
            device.printText("\n");
            device.printText("printBitmap end.");
            device.cutPaper();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        //   D3usbPrinter.writePort(new byte[]{29, 86, 0}, 3);
    }

    public void printerPrintAndImg(View v) {
        if (device == null) {
            Toast.makeText(MainActivity.this, "please open printer", Toast.LENGTH_SHORT).show();
            return;
        }
        PurchaseBillForQ1Entity purchaseBill = new PurchaseBillForQ1Entity();
        setPurchaseBillForQ1Entity(purchaseBill);
        try {
            printBillForQ1(purchaseBill);
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(mContext.getResources().getAssets().open("weixinxxxx.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            device.printBitmap(bitmap);
            bitmap.recycle();
            device.printText("\n");
            device.printText("\n");
            device.printText("\n");
            device.cutPaper();
        } catch (DeviceException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void printBillForQ1(PurchaseBillForQ1Entity purchaseBill) throws DeviceException {
        device.sendESCCommand(PrinterCommand.getCmdEsc3N(10));
        device.sendESCCommand(PrinterCommand.getCmdSetSmallFont_CN());
        device.sendESCCommand(PrinterCommand.getCmdSetSmallFont_EN());
        device.sendESCCommand(PrinterCommand.getCmdEscDN(2));
        Format format = new Format();
        format.setParameter("size", "small");
        device.printText(format, PrintTagForQ1.PurchaseBillTag.MERCHANT_COPY_TAG + "\n");
        device.printText(PrintTagForQ1.PurchaseBillTag.SEPARATE + "\n");
        device.printText(PrintTagForQ1.PurchaseBillTag.MERCHANT_NAME_TAG + "\n");
        format.clear();
        format.setParameter("size", "MEDIUM");
        device.printText(format, purchaseBill.getMerchantName() + "\n");
        format.clear();
        format.setParameter("size", "small");
        device.printText(format, PrintTagForQ1.PurchaseBillTag.MERCHANT_NO_TAG + "\n");
        device.printText(purchaseBill.getMerchantNo() + "\n");
        device.printText(PrintTagForQ1.PurchaseBillTag.TERMINAL_NO_AND_OPERATOR_TAG + "\n");
        device.printText(purchaseBill.getTerminalNoAndOperator());
        device.printText(PrintTagForQ1.PurchaseBillTag.SEPARATE + "\n");
        device.printText(PrintTagForQ1.PurchaseBillTag.CARD_NO_TAG + "\n");
        format.clear();
        format.setParameter("size", "MEDIUM");
        device.printText(format, purchaseBill.getCardNo());
        format.clear();
        format.setParameter("size", "small");
        device.printText(format, PrintTagForQ1.PurchaseBillTag.ISSUER_ACQUIRER_TAG + "\n");
        device.printText(purchaseBill.getIssuerAndAcquirer());
        device.printText(PrintTagForQ1.PurchaseBillTag.TRANS_TYPE_TAG + "\n");
        format.clear();
        format.setParameter("size", "MEDIUM");
        device.printText(format, purchaseBill.getTransType());
        format.clear();
        format.setParameter("size", "small");
        device.printText(format, PrintTagForQ1.PurchaseBillTag.SEPARATE + "\n");
        device.printText(PrintTagForQ1.PurchaseBillTag.DATE_TIME_EXP_DATE_TAG + "\n");
        device.printText(purchaseBill.getDataTimeAndExpDate());
        device.printText(PrintTagForQ1.PurchaseBillTag.REF_NO_BATCH_NO_TAG + "\n");
        device.printText(purchaseBill.getRefNoAndBatchNo());
        device.printText(PrintTagForQ1.PurchaseBillTag.VOUCHER_NO_AUTH_NO_TAG);
        device.printText(purchaseBill.getVoucherAndAuthNo());
        device.printText(PrintTagForQ1.PurchaseBillTag.AMOUT_TAG + "\n");
        format.clear();
        format.setParameter("size", "MEDIUM");
        device.printText(format, purchaseBill.getAmout());
        format.clear();
        format.setParameter("size", "small");
        device.printText(format, PrintTagForQ1.PurchaseBillTag.SEPARATE + "\n");
        device.printText(PrintTagForQ1.PurchaseBillTag.REFERENCE_TAG + "\n");
        String[] referenceStrings = purchaseBill.getReference().split(",");
        for (String string : referenceStrings) {
            device.printText(string);
        }
        format.clear();
        format.setParameter("size", "MEDIUM");
        device.printText(format, PrintTagForQ1.PurchaseBillTag.CARD_HOLDER_SIGNATURE_TAG + "\n");
        device.sendESCCommand(PrinterCommand.getCmdEscDN(3));
        format.clear();
        format.setParameter("size", "small");
        device.printText(format, PrintTagForQ1.PurchaseBillTag.SEPARATE + "\n");
        device.printText(PrintTagForQ1.PurchaseBillTag.C_AGREE_TRADE_TAG + "\n");
        device.printText(PrintTagForQ1.PurchaseBillTag.E_AGREE_TRADE_TAG + "\n");
        device.sendESCCommand(PrinterCommand.getCmdEscDN(2));
    }

    private void setPurchaseBillForQ1Entity(PurchaseBillForQ1Entity purchaseBill) {
        purchaseBill.setMerchantName("SHANGHAIHUIYINXINXIKEJIYOUXIANGONGSI\n");
        purchaseBill.setMerchantNo("530310041315039\n");
        purchaseBill.setTerminalNoAndOperator("50000045        50000045\n");
        purchaseBill.setCardNo("623020xxxxxx3994    I\n");
        purchaseBill.setIssuerAndAcquirer("   \n");
        purchaseBill.setTransType("SALE\n");
        purchaseBill.setDataTimeAndExpDate("2005/01/21 16:52:32        2099/12\n");
        purchaseBill.setRefNoAndBatchNo("165232857468        000001\n");
        purchaseBill.setVoucherAndAuthNo("000042\n");
        purchaseBill.setAmout("RMB:0.01\n");
        purchaseBill.setReference("SCN:01" + ",UMPR NUM:4F682D56" + ",TC:EF789E918A548668" + ",TUR:008004E000" + ",AID:A000000333010101" + ",TSI:F800    ATC:0440" + ",APPLAB:PBOC  DEBIT" + ",APPNAME:PBOC  DEBIT" + ",AIP:7C00    CUMR:020300" + ",IAD:07010103602002010A01000000000005DD79CB" + ",TermCap:EOE1C8");
    }


    public void printerPrintBarcode(View v) {
        try {
            Format format = new Format();
            format.setParameter("HRI-location", "UP");
            if (device == null) {
                Toast.makeText(MainActivity.this, "please open printer", Toast.LENGTH_SHORT).show();
                return;
            }

            device.printBarcode(format, PrinterDevice.BARCODE_CODE128, new Random().nextInt(2000000000) + "");
            device.printText("\n");
            device.printText("\n");
            device.printText("\n");
            format.setParameter("HRI-location", "DOWN");
            device.printBarcode(format, PrinterDevice.BARCODE_CODE128, new Random().nextInt(2000000000) + "");
            device.printText("\n");
            device.printText("\n");
            device.printText("\n");
            device.cutPaper();
        } catch (DeviceException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Logger.d("code is not supported");
        }
    }

    public void openCashbox(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final CashDrawerDevice device = (CashDrawerDevice) POSTerminal.getInstance(mContext).getDevice("cloudpos.device.cashdrawer");
                try {
                    device.open();

                    try {
                        device.kickOut();
                    } catch (DeviceException e) {
                        e.printStackTrace();
                    }
                    try {
                        device.close();
                    } catch (DeviceException e) {
                        e.printStackTrace();
                    }
                } catch (DeviceException e) {
//                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void closePrinter(View view) {
        try {
            if (device == null) {
                Toast.makeText(MainActivity.this, "please open printer", Toast.LENGTH_SHORT).show();
                return;
            }
            device.close();
            device = null;
            Toast.makeText(MainActivity.this, "printer already closed", Toast.LENGTH_SHORT).show();
        } catch (DeviceException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public String parsePrinterStatus2Description(int priterStatus) {
        switch (priterStatus) {
            case 0:
                return "0: normal";
            case 1:
                return "1: Data transmission error, please check connection or resend";
            case 2:
                return "2: Less paper";
            case 3:
                return "3: Missing paper";
            case 4:
                return "4: Error occurred";
            case 5:
                return "5: Open the cover";
            case 6:
                return "6: Offline";
            case 7:
                return "7: Feeding";
            case 8:
                return "8: Mechanical error";
            case 9:
                return "9: Automatically recoverable error";
            case 10:
                return "10: Unrecoverable error";
            case 11:
                return "11: Print head overheating";
            case 12:
                return "12: Cutter not reset";
            case 13:
                return "13: Black mark error";
            default:
                return "printer status code is not defined = " + priterStatus;
        }
    }

    public void printHtml(View view) {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (!Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivityForResult(intent, 1);
//                return;
//            } else {
//                //TODO do something you need
//            }
//        }
        try {
            device.printText(new Format(), "\n");

            device.printHTML(this, "\n" +
                    "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "\n" +
                    "<body>\n" +
                    "<style type=\"text/css\">\n" +
                    "    *{margin : 0px; padding : 0px; width : 576px;}\n" +
                    "</style>\n" +
                    "<h3>It has some limitations, but it's a start.</h3>\n" +
                    "<h3>alipay.png</h3>\n" +
                    "<!--<img src=\"file:///android_asset/alipay.png\" width=\"360px\">-->\n" +
                    "Lorem ipsum dolor sit amet,\n" +
                    "ea nec esse case fabulas.\n" +
                    "n sit ullum elaboraret.\n" +
                    "Aeque salutatus suscipiantur nec an.\n" +
                    "Ut sit unum scribentur,\n" +
                    "ut per habeo legendos.\n" +
                    "Case tibique quo et.\n" +
                    "Ea duo adhuc zril blandit,\n" +
                    "eam ei dolore impetus vidisse,\n" +
                    "at nec dignissim intellegat.\n" +
                    "<h3>onecodepic.png</h3>\n" +
                    "<!--<img src=\"file:///android_asset/onecodepic.png\" width=\"360px\">-->\n" +
                    "Id ignota feugait duo,\n" +
                    "id vis labores insolens singulis,\n" +
                    "sed officiis voluptatum et.\n" +
                    "Mel nobis moderatius et.\n" +
                    "Ea prompta partiendo eam,\n" +
                    "quando tamquam volumus ne nam.\n" +
                    "At etiam theophrastus vim,\n" +
                    "<h3>triangle_center.bmp</h3>\n" +
                    "<!--<img src=\"file:///android_asset/triangle_center.bmp\" width=\"360px\">-->\n" +
                    "pertinacia vituperatoribus usu eu.\n" +
                    "Dicta verterem per ei,\n" +
                    "ne eam quodsi noluisse honestatis.\n" +
                    "Vel dico reque consequat an.\n" +
                    "In nam libris blandit complectitur,\n" +
                    "electram accusamus vix ut.\n" +
                    "Alii argumentum quaerendum qui ea,\n" +
                    "vim nobis invenire torquatos ex,\n" +
                    "wisi eruditi invenire an eum.\n" +
                    "Ad cum homero admodum.\n" +
                    "</body>\n" +
                    "</html>", new PrinterHtmlListener() {
                @Override
                public void onGet(Bitmap bitmap, int i) {

                }

                @Override
                public void onFinishPrinting(int i) {
                    Toast.makeText(MainActivity.this, "printer end", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
