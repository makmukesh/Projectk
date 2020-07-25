package com.vpipl.kalpamrit.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.vpipl.kalpamrit.R;
import com.vpipl.kalpamrit.Utils.AppUtils;
import com.vpipl.kalpamrit.Utils.TextDrawable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mukesh on 03-07-2020.
 */
public class Download_Adapter extends RecyclerView.Adapter<Download_Adapter.MyViewHolder> {
    private Context context;
    public static ArrayList<HashMap<String, String>> array_list;
    private LayoutInflater inflater = null;
    private TextDrawable.IBuilder mDrawableBuilder;
    String str_filename;

    public Download_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_list = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
        mDrawableBuilder = TextDrawable.builder().round();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_download, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {

            holder.txt_download_name.setText(array_list.get(position).get("FileHeading"));
            //holder.txt_name.setText(array_list.get(position).get("File_URL"));
            holder.txt_download_name.setSelected(true);
            holder.txt_download_name.setSingleLine(true);

            int abc = context.getResources().getColor(R.color.color_green_text);
            holder.drawable123 = mDrawableBuilder.build(String.valueOf(array_list.get(position).get("FileHeading").trim().charAt(0)).toUpperCase(), abc);
            holder.letter_icon.setImageDrawable(holder.drawable123);

            holder.download_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    str_filename = array_list.get(position).get("File_URL");
                    new DownloadFile().execute(str_filename, "maven.pdf");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_download_name;
        ImageView letter_icon, download_icon;
        TextDrawable drawable123;

        public MyViewHolder(View view) {
            super(view);
            letter_icon = (ImageView) view.findViewById(R.id.letter_icon);
            download_icon = (ImageView) view.findViewById(R.id.download_icon);
            txt_download_name = (TextView) view.findViewById(R.id.txt_download_name);
        }
    }

    public class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                //  String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name

                //   fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "Kalpamrit/";

                str_filename = "Kalpamrit/" + fileName;

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d("TAG", "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                // return "Downloaded at: " + folder + fileName;
                return "" + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }


        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading

            alertDialog(context, message, str_filename);
         //   Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    public static void alertDialog(final Context context, final String message, final String str_filename) {
        try {
            final Dialog dialog = AppUtils.createDialog(context, false);
            TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            //   dialog4all_txt.setText(message);
            //  dialog4all_txt.setText(str_filename);
            dialog4all_txt.setText("Your file completed download and view from file manager!!");

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Open");
            txt_submit.setVisibility(View.GONE);
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("PDFURL2", message);
                    File file = new File(message);

                    PackageManager packageManager = context.getPackageManager();
                    Intent testIntent = new Intent(Intent.ACTION_VIEW);
                    testIntent.setType("application/pdf");
                    List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (list.size() > 0 && file.isFile()) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(file);
                        intent.setDataAndType(uri, "application/pdf");
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "PDF Reader not available !!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });
            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText("Ok");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}