package com.bolyartech.forge.admin.units.admin_user.admin_users_list;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.misc.ViewUtils;

import java.util.List;


public class AdminUsersAdapter extends ArrayAdapter<AdminUser> {
    private final Activity mActivity;

    public AdminUsersAdapter(Context context, int resource, List<AdminUser> objects) {
        super(context, resource, objects);
        mActivity = (Activity) context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.lvr__admin_users, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.username = ViewUtils.findTextViewX(rowView, R.id.tv_username);
            viewHolder.name = ViewUtils.findTextViewX(rowView, R.id.tv_name);
            viewHolder.disabled = ViewUtils.findImageViewX(rowView, R.id.iv_disabled);
            viewHolder.superadmin = ViewUtils.findImageViewX(rowView, R.id.iv_superadmin);
            rowView.setTag(viewHolder);
        }


        ViewHolder holder = (ViewHolder) rowView.getTag();
        AdminUser item = getItem(position);

        holder.name.setText(item.getName());
        holder.username.setText(item.getUsername());

        if (item.isDisabled()) {
            holder.disabled.setVisibility(View.VISIBLE);
        } else {
            holder.disabled.setVisibility(View.INVISIBLE);
        }

        if (item.isSuperAdmin()) {
            holder.superadmin.setVisibility(View.VISIBLE);
        } else {
            holder.superadmin.setVisibility(View.INVISIBLE);
        }


        return rowView;
    }


    private static class ViewHolder {
        TextView username;
        TextView name;
        ImageView disabled;
        ImageView superadmin;
    }
}
