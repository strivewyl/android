package com.ly.university.assistant;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
 
/**
 * 
 * Create custom Dialog windows for your application
 * Custom dialogs rely on custom layouts wich allow you to 
 * create and use your own look & feel.
 * 显示详细信息
 * @author 王亚磊
 *
 */
public class C_A_CustomDialog extends Dialog {
 
    public C_A_CustomDialog(Context context, int theme) {
        super(context, theme);
    }
 
    public C_A_CustomDialog(Context context) {
        super(context);
    }
 
    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
 
        private Context context;
        private String title;
        private String subject;
        private String address;
        private String start_time;
        private String end_time;
       // private View contentView;  用于显示空内容
 
        private DialogInterface.OnClickListener 
                        positiveButtonClickListener;
 
        public Builder(Context context) {
            this.context = context;
        }
 
        /**
         * Set the Dialog message from String
         * @param title
         * @return
         */
        public Builder setMessage(String subject, String address, String start_time, String end_time) {
            this.subject=subject;
            this.address=address;
            this.start_time=start_time;
            this.end_time=end_time;
            return this;
        }
 
//        /**
//         * Set the Dialog message from resource
//         * @param title
//         * @return
//         */
//        public Builder setMessage(int message) {
//            this.message = (String) context.getText(message);
//            return this;
//        }
// 
//        /**
//         * Set the Dialog title from resource
//         * @param title
//         * @return
//         */
//        public Builder setTitle(int title) {
//            this.title = (String) context.getText(title);
//            return this;
//        }
 
        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
 
        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v
         * @return
         */
//        public Builder setContentView(View v) {
//            this.contentView = v;
//            return this;
//        }
 
        /**
         * Set the positive button resource and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButtonListener(DialogInterface.OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }
 
//        /**
//         * Set the positive button text and it's listener
//         * @param positiveButtonText
//         * @param listener
//         * @return
//         */
//        public Builder setPositiveButton(String positiveButtonText,
//                DialogInterface.OnClickListener listener) {
//            this.positiveButtonText = positiveButtonText;
//            this.positiveButtonClickListener = listener;
//            return this;
//        }
// 
        /**
         * Create the custom dialog
         */
        public C_A_CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final C_A_CustomDialog dialog = new C_A_CustomDialog(context, 
            		R.style.dialog);
            View layout = inflater.inflate(R.layout.c_a_dialog_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button
//            if (positiveButtonText != null) {
//                ((Button) layout.findViewById(R.id.positiveButton))
//                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
//            } 
//        else {
//                // if no confirm button just set the visibility to GONE
//                layout.findViewById(R.id.positiveButton).setVisibility(
//                        View.GONE);
//            }
            // set the cancel button
            // set the content message
//            if (subject != null) {
//                ((TextView) layout.findViewById(
//                		R.id.subject)).setText(subject);
//            } else if (contentView != null) {
//                // if no message set
//                // add the contentView to the dialog body
//                ((LinearLayout) layout.findViewById(R.id.content))
//                        .removeAllViews();
//                ((LinearLayout) layout.findViewById(R.id.content))
//                        .addView(contentView, 
//                                new LayoutParams(
//                                        LayoutParams.WRAP_CONTENT, 
//                                        LayoutParams.WRAP_CONTENT));
//            }
            ((TextView) layout.findViewById(R.id.subject)).setText(subject);
            ((TextView) layout.findViewById(R.id.address)).setText(address);
            ((TextView) layout.findViewById(R.id.start_time)).setText(start_time);
            ((TextView) layout.findViewById(R.id.end_time)).setText(end_time);  
            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(true);
            return dialog;
        }
 
    }
 
}