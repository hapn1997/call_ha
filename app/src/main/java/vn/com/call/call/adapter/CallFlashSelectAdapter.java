package vn.com.call.call.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.dialer.ios.iphone.contacts.R;
import vn.com.call.call.model.CallFlashItem;
import vn.com.call.ui.BaseActivity;
import vn.com.call.App;
import vn.com.call.call.model.CallFlashItem;
import vn.com.call.ui.BaseActivity;
import vn.com.call.utils.DeviceUtil;

public class CallFlashSelectAdapter extends RecyclerView.Adapter<CallFlashSelectAdapter.ViewHolder> {

    private BaseActivity activity;
    private Picasso picasso;
    private ArrayList<CallFlashItem> listItem;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private CallFlashItem defaultItem;

    public CallFlashSelectAdapter(BaseActivity activity, ArrayList<CallFlashItem> listItem) {
        this.activity = activity;
        this.listItem = listItem;
        this.mInflater = LayoutInflater.from(activity);
        picasso = ((App) activity.getApplication()).getPicasso();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_setting_callflash, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CallFlashItem item = listItem.get(position);

        picasso.load(item.getThumb()).resize(500, 700).centerCrop().into(holder.imgBg);
        if (defaultItem != null && defaultItem.getId().equals(item.getId())) {
            holder.imgChecked.setVisibility(View.VISIBLE);
        } else {
            holder.imgChecked.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View layout;
        ImageView imgBg, imgChecked;

        ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_setting_callflash_layout);
            imgBg = itemView.findViewById(R.id.item_setting_callflash_image_bg);
            imgChecked = itemView.findViewById(R.id.item_setting_callflash_image_checked);
            layout.setOnClickListener(this);

            int widthHalf = DeviceUtil.getWidthScreen(activity) / 2;
            layout.getLayoutParams().height = (int) (widthHalf * 1.5);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setCallFlashItem(CallFlashItem callFlashItem) {
        defaultItem = callFlashItem;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}