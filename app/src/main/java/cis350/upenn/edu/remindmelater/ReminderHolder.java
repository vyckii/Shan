package cis350.upenn.edu.remindmelater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cis350.upenn.edu.remindmelater.Activities.EditReminderActivity;

/**
 * Created by AJNandi on 3/15/17.
 *
 * This class maps the reminder data to its respective text views etc
 *
 */

public class ReminderHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

    private final TextView reminderTitle;
    private final TextView reminderDesc;
    private String reminderDueDate;
    private final TextView reminderTime;
    private final TextView reminderType;
    private final TextView reminderLoc;
    private String reminderRec;
    private String reminderRecDate;
    private ImageView reminderImage;
    private byte[] imageAsBytes;

    private Context context;

    public ReminderHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        context = itemView.getContext();

        //TODO: Add views for this and put their IDs in place
        reminderTitle = (TextView) itemView.findViewById(R.id.reminder_title);
        reminderDesc = (TextView) itemView.findViewById(R.id.reminder_desc);
        reminderTime =  (TextView) itemView.findViewById(R.id.due_date_label);
        reminderType =  (TextView) itemView.findViewById(R.id.reminder_type);
        reminderLoc =  (TextView) itemView.findViewById(R.id.reminder_loc);
        reminderImage = (ImageView) itemView.findViewById(R.id.reminder_image);
    }

    public void setReminderTitle(String name) {
        reminderTitle.setText(name);
    }

    public void setReminderDesc(String text) {
        reminderDesc.setText(text);
    }

    public void setReminderTime(Long dateStr) {

        reminderDueDate = dateStr.toString();

        if (dateStr != null) {
            SimpleDateFormat f1 = new SimpleDateFormat("hh:mm a", Locale.US);
            SimpleDateFormat f2 = new SimpleDateFormat("EEEE, MMMM d", Locale.US);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateStr);
            reminderTime.setText("" + f1.format(calendar.getTime()) + "\n" + f2.format(calendar.getTime()));
        } else {
            reminderTime.setText("No Date Set");
        }
    }

    public void setReminderType(String text) {
        reminderType.setText(text);
    }

    public void setReminderLoc(String text) {
        reminderLoc.setText(text);
    }

    public void setReminderRec(String text) {
        reminderRec = text;
    }

    public void setReminderRecDate(String text) {
        reminderRecDate = text;
    }

    public void setReminderImage(String text) {
        if (!text.equals("")) {
            Bitmap image = decodeFromFirebaseBase64(text);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageAsBytes = stream.toByteArray();

            reminderImage.setImageBitmap(image);
        }
    }


    public void onClick(View view) {
        System.out.println("clicked " + reminderTitle.getText());

        Intent intent = new Intent(context, EditReminderActivity.class);
        intent.putExtra("reminderName", reminderTitle.getText());
        intent.putExtra("notes", reminderDesc.getText());
        intent.putExtra("dueDate", reminderDueDate);
        intent.putExtra("recurring", reminderRec);
        intent.putExtra("recurringUntil", reminderRecDate);
        intent.putExtra("category", reminderType.getText());
        intent.putExtra("location", reminderLoc.getText());
        intent.putExtra("imageBytes", imageAsBytes);
        context.startActivity(intent);
    }

    public static Bitmap decodeFromFirebaseBase64(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

}
