package com.bolyartech.forge.admin.units.user.users;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.android.misc.ViewUtils;

import java.util.List;


public class UsersAdapter extends ArrayAdapter<User> {
    private final Activity mActivity;

    public UsersAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        mActivity = (Activity) context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.lvr__users, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.username = ViewUtils.findTextViewX(rowView, R.id.tv_username);
            viewHolder.screen_name = ViewUtils.findTextViewX(rowView, R.id.tv_screen_name);
            viewHolder.disabled = ViewUtils.findImageViewX(rowView, R.id.iv_disabled);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        User item = getItem(position);

        holder.screen_name.setText(item.getScreenName());
        holder.username.setText(item.getUsername());

        if (item.isDisabled()) {
            holder.disabled.setVisibility(View.VISIBLE);
        } else {
            holder.disabled.setVisibility(View.INVISIBLE);
        }

        return rowView;
    }


    private static class ViewHolder {
        TextView username;
        TextView screen_name;
        ImageView disabled;
    }
}
