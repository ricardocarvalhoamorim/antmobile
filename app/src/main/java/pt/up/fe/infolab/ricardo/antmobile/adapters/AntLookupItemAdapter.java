package pt.up.fe.infolab.ricardo.antmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import pt.up.fe.infolab.ricardo.antmobile.AppController;
import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.Utils;
import pt.up.fe.infolab.ricardo.antmobile.models.Data;
import pt.up.fe.infolab.ricardo.antmobile.models.Decoration;
import pt.up.fe.infolab.ricardo.antmobile.models.ResponseAttribute;
import pt.up.fe.infolab.ricardo.antmobile.models.SearchResult;

public class AntLookupItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<SearchResult> items;
    private int lastPosition = -1;


    public AntLookupItemAdapter(ArrayList<SearchResult> srcItems,
                                Context context) {

        this.context = context;
        this.items = srcItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.ant_response_item, parent, false);
        return new AntLookupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final SearchResult item = items.get(position);
        ((AntLookupViewHolder)holder).tvItemName.setText(item.getDescription());

        String baseQuery = Utils.antEndpoint + "/search/decorator/metadata?";
        Uri builtUri = Uri.parse(baseQuery)
                .buildUpon()
                .appendQueryParameter("entity", item.getUri().substring(item.getUri().indexOf('#') + 1))
                .appendQueryParameter("type", item.getType().getUri().substring(item.getType().getUri().indexOf('#') + 1))
                .build();

        String queryURL = builtUri.toString();


        ((AntLookupViewHolder)holder).tvExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((AntLookupViewHolder) holder).tvItemAttributes.getVisibility() == View.VISIBLE) {
                    ((AntLookupViewHolder) holder).tvItemAttributes.setVisibility(View.GONE);
                    ((AntLookupViewHolder)holder).tvExpand.setText(context.getString(R.string.more));
                } else {
                    ((AntLookupViewHolder) holder).tvItemAttributes.setVisibility(View.VISIBLE);
                    ((AntLookupViewHolder)holder).tvExpand.setText(context.getString(R.string.less));
                }
            }
        });


        ((AntLookupViewHolder) holder).tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = item.getLink();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, queryURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Decoration responseObject;
                try {
                    Data responseData = new Gson().fromJson(response.get("data").toString(), Data.class);
                    responseObject = responseData.getDecorations();
                } catch (JSONException e) {
                    Log.e("JSON", e.getMessage());
                    return;
                }

                //Set item attributes in the display
                String attributesString = "";
                HashMap<String, String> attributes = new HashMap<>();
                for (ResponseAttribute rt : responseObject.getAttributes()) {
                    attributes.put(rt.getLabel(), rt.getValue().toString());
                    attributesString += "<b>" + rt.getLabel() + ":</b> " +
                            rt.getValue() + "<br />";
                }



                if (!item.getSources().isEmpty()) {
                    String formatedSources ="";
                    for (String s : item.getSources()) {
                        formatedSources += s + "\n";
                    }
                    ((AntLookupViewHolder)holder).tvItemRole.setText(formatedSources);
                    ((AntLookupViewHolder)holder).tvItemRole.setVisibility(View.VISIBLE);
                } else {
                    ((AntLookupViewHolder)holder).tvItemRole.setVisibility(View.GONE);
                }

                if (attributes.containsKey("Sítio")) {
                    ((AntLookupViewHolder)holder).tvWebpage.setVisibility(View.VISIBLE);
                    ((AntLookupViewHolder)holder).tvWebpage.setText(Html.fromHtml("<a href=" + attributes.get("Sítio") + "/>"));
                }

                if (attributes.containsKey("Sala")) {
                    ((AntLookupViewHolder)holder).tvRoom.setVisibility(View.VISIBLE);
                    ((AntLookupViewHolder)holder).tvRoom.setText(attributes.get("Sala"));
                }

                for (ArrayList<ResponseAttribute> arr : responseObject.getLevelTwoAttributes()) {
                    for (ResponseAttribute rAttr : arr) {
                        attributesString += "<b>" + rAttr.getLabel() + ":</b> " +
                                rAttr.getValue() + "<br />";
                    }
                    attributesString += "<br />";
                }

                ((AntLookupViewHolder)holder).tvItemAttributes.setText(Html.fromHtml(attributesString));
                ((AntLookupViewHolder)holder).ivItemDrawable.setVisibility(View.GONE);
                if (!Utils.antPeople.contains(item.getType().getUri()))
                    return;

                Glide.with(context).load(responseObject.getPhoto())
                        .asBitmap()
                        .centerCrop()
                        .animate(android.R.anim.fade_in)
                        .into(new BitmapImageViewTarget(((AntLookupViewHolder) holder).ivItemDrawable) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                ((AntLookupViewHolder)holder).ivItemDrawable.setVisibility(View.VISIBLE);
                                super.onResourceReady(bitmap, anim);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                ((AntLookupViewHolder)holder).ivItemDrawable.setVisibility(View.GONE);
                                //super.onLoadFailed(e, errorDrawable);
                            }
                        });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        setAnimation(((AntLookupViewHolder) holder).itemContainer, position);
    }



    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class AntLookupViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private TextView tvItemName;
        private TextView tvItemAttributes;
        private TextView tvItemRole;
        private TextView tvWebpage;
        private TextView tvRoom;
        private TextView tvExpand;
        private TextView tvOpen;
        private ImageView ivItemDrawable;
        private CardView itemContainer;

        public AntLookupViewHolder(View rowView) {
            super(rowView);

            tvItemName = (TextView) rowView.findViewById(R.id.item_name);
            tvItemAttributes = (TextView) rowView.findViewById(R.id.item_attributes);
            ivItemDrawable = (ImageView) rowView.findViewById(R.id.item_photo);
            tvWebpage = (TextView) rowView.findViewById(R.id.item_webpage);
            itemContainer = (CardView) rowView.findViewById(R.id.item_container);
            tvItemRole = (TextView) rowView.findViewById(R.id.item_role);
            tvRoom = (TextView) rowView.findViewById(R.id.item_room);
            tvExpand = (TextView) rowView.findViewById(R.id.item_expand);
            tvOpen = (TextView) rowView.findViewById(R.id.item_open);

            rowView.setOnClickListener(this);
            rowView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }


    private void setAnimation(View viewToAnimate, int position) {


        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
