package com.teamtreehouse.albumcover;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Explode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AlbumListActivity extends Activity {

    @Bind(R.id.album_list) RecyclerView mAlbumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);

        setupTransitions();

        ButterKnife.bind(this);
        populate();
    }

    private void setupTransitions() {
        // when this activity starts another activity
//        getWindow().setExitTransition(new Explode());
    }

    interface OnVHClickedListener {
        void onVHClicked(AlbumVH vh);
    }

    static class AlbumVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final OnVHClickedListener mListener;
        @Bind(R.id.album_art)
        ImageView albumArt;

        public AlbumVH(View itemView, OnVHClickedListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            mListener.onVHClicked(this);
        }
    }

    private void populate() {
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mAlbumList.setLayoutManager(lm);

        final int[] albumArts = {
                R.drawable.mean_something_kinder_than_wolves,
                R.drawable.cylinders_chris_zabriskie,
                R.drawable.broken_distance_sutro,
                R.drawable.playing_with_scratches_ruckus_roboticus,
                R.drawable.keep_it_together_guster,
                R.drawable.the_carpenter_avett_brothers,
                R.drawable.please_sondre_lerche,
                R.drawable.direct_to_video_chris_zabriskie };

        RecyclerView.Adapter adapter = new RecyclerView.Adapter<AlbumVH>() {
            @Override
            public AlbumVH onCreateViewHolder(ViewGroup parent, int viewType) {

                View albumView = getLayoutInflater().inflate(R.layout.album_grid_item, parent, false);

                return new AlbumVH(albumView, new OnVHClickedListener() {
                    @Override
                    public void onVHClicked(AlbumVH vh) {
                        int albumArtResId = albumArts[vh.getPosition() % albumArts.length];
                        Intent intent = new Intent(AlbumListActivity.this, AlbumDetailActivity.class);
                        intent.putExtra(AlbumDetailActivity.EXTRA_ALBUM_ART_RESID, albumArtResId);

                        // Create an ActivityOptions to transition between Activities using cross-Activity scene animations.
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                AlbumListActivity.this,
                                vh.albumArt,  // Shared element object. Used to capture initial state
                                "albumArt");  // Shared element's name. Used to detect shared view (id wont work in some cases)
                        // Shared elements must have android:transitionName="albumArt" attribute
                        // in both activities layout files to calculate it's initial and planned state
                        // then apply according animation
                        // NOTE: SHARED ELEMENT IS AN ILLUSION. NO VIEWS ARE REALLY SHARED AT ALL

                        // Pass options in bundle along with intent
                        startActivity(intent, options.toBundle());
                    }
                });
            }

            @Override
            public void onBindViewHolder(AlbumVH holder, int position) {
                holder.albumArt.setImageResource(albumArts[position % albumArts.length]);
            }

            @Override
            public int getItemCount() {
                return albumArts.length * 4;
            }

        };
        mAlbumList.setAdapter(adapter);
    }

}
