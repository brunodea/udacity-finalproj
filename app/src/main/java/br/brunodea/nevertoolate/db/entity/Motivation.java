package br.brunodea.nevertoolate.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

@Entity
public class Motivation {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull @TypeConverters(MotivationTypeConverter.class)
    public final MotivationType type; // In the future we may want something other than only images!
    @NonNull
    public final int motivationId; // Id to the actual motivation
    public final boolean favorite; // Is this motivation favorited?
    public final int notificationId; // Id of the notification that triggered this motivation. Can be NULL.

    enum MotivationType {
        IMAGE(1);

        private int code;
        MotivationType(int code) {
            this.code = code;
        }
        public int code() {
            return this.code;
        }
    }

    public Motivation(final MotivationType type, final int motivationId, final boolean favorite, final int notificationId) {
        this.type = type;
        this.motivationId = motivationId;
        this.favorite = favorite;
        this.notificationId = notificationId;
    }
}
