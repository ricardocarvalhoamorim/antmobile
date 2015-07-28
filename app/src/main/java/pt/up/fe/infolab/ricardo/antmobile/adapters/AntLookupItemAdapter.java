package pt.up.fe.infolab.ricardo.antmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;

import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.models.SigarraAttribute;
import pt.up.fe.infolab.ricardo.antmobile.models.SigarraIndividual;

public class AntLookupItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<SigarraIndividual> items;
    private int lastPosition = -1;

    private static AdapterViewCompat.OnItemClickListener listener;


    public AntLookupItemAdapter(ArrayList<SigarraIndividual> srcItems,
                                AdapterViewCompat.OnItemClickListener clickListener,
                                Context context) {

        this.context = context;
        this.items = srcItems;
        listener = clickListener;
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

        final SigarraIndividual item = items.get(position);
        String room = "";

        String attributesStr = "";
        String id = "";
        ArrayList<SigarraAttribute> attributes = item.getAttributes();

        for (SigarraAttribute attr : attributes) {
            attributesStr += attr.getLabel() + ": " + attr.getValue() + "\n";
            if (attr.getLabel().equals("Código")) {
                id = attr.getValue();
            }

            if (attr.getLabel().equals("Sala")) {
                room = attr.getValue();
            }
        }

        ((AntLookupViewHolder)holder).tvItemName.setText(item.getDescription());
        ((AntLookupViewHolder)holder).tvItemAttributes.setText(attributesStr);
        ((AntLookupViewHolder)holder).pbItemScore.setProgress((int) item.getScore());

        if (room.equals("")) {
            ((AntLookupViewHolder)holder).itemMap.setVisibility(View.GONE);
        } else {
            ((AntLookupViewHolder)holder).itemMap.setVisibility(View.VISIBLE);
        }

        if (!id.equals("")) {
            String thumbUrl = "https://sigarra.up.pt/feup/en/FOTOGRAFIAS_SERVICE.foto?pct_cod="
                    + id;

            Glide.with(context).load(thumbUrl)
                    .asBitmap()
                    .centerCrop()
                    .animate(android.R.anim.fade_in)
                    .placeholder(R.drawable.ic_person_dark)
                    .into(new BitmapImageViewTarget(((AntLookupViewHolder) holder).ivItemDrawable) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            super.onResourceReady(bitmap, anim);
                            /*
                            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    // Here's your generated palette

                                    holder.rlItemLegend.setBackgroundColor(
                                            palette.getLightVibrantColor(R.color.text_blue_grey_darker));

                                    holder.rlItemLegend.setVisibility(View.VISIBLE);
                                }
                            });
                            */
                        }
                    });

            //setAnimation(holder.itemContainer, position);
        } else {
            ((AntLookupViewHolder)holder).ivItemDrawable.setImageDrawable(
                    context.getDrawable(R.drawable.ic_person_dark));
        }

        setAnimation(((AntLookupViewHolder)holder).itemContainer, position);

        ((AntLookupViewHolder)holder).itemShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((AntLookupViewHolder)holder).itemWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((AntLookupViewHolder)holder).itemMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (SigarraAttribute attr : item.getAttributes()) {
                    if (attr.getLabel().equals("Sala")) {
                        String url = "http://maps.google.com/maps?daddr="+ attr.getValue() + ", Faculdade de Engenharia da Universidade do Porto";
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  Uri.parse(url));
                        context.startActivity(intent);
                        return;
                    }
                }
            }
        });
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
        private ProgressBar pbItemScore;
        private ImageView ivItemDrawable;
        private CardView itemContainer;

        private ImageButton itemMap;
        private ImageButton itemWeb;
        private ImageButton itemShare;

        public AntLookupViewHolder(View rowView) {
            super(rowView);

            tvItemName = (TextView) rowView.findViewById(R.id.ant_item_name);
            tvItemAttributes = (TextView) rowView.findViewById(R.id.ant_item_category);
            ivItemDrawable = (ImageView) rowView.findViewById(R.id.ant_item_photo);
            pbItemScore = (ProgressBar) rowView.findViewById(R.id.item_score);
            itemContainer = (CardView) rowView.findViewById(R.id.item_container);

            itemMap = (ImageButton) rowView.findViewById(R.id.item_action_map);
            itemWeb = (ImageButton) rowView.findViewById(R.id.item_action_info);
            itemShare = (ImageButton) rowView.findViewById(R.id.item_action_share);

            rowView.setOnClickListener(this);
            rowView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //listener.onItemClick(view, getPosition());
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
