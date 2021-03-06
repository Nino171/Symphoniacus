package at.fhv.teamb.symphoniacus.application.dto.wishdtos;

import at.fhv.teamb.symphoniacus.application.type.WishStatusType;
import at.fhv.teamb.symphoniacus.application.type.WishTargetType;
import at.fhv.teamb.symphoniacus.application.type.WishType;

/**
 * Dto class for all kinds of wishes.
 *
 * @author Tobias Moser
 */
public class WishDto<T> {
    private Integer wishId;
    private WishType wishType;
    private WishTargetType target;
    private WishStatusType status;
    private String reason;
    private T details;


    public WishDto(){}

    /**
     * Constructor for WishDto.
     */
    public WishDto(
            Integer wishId,
            WishType wishType,
            WishTargetType target,
            WishStatusType status,
            String reason,
            T details
    ) {
        this.wishId = wishId;
        this.wishType = wishType;
        this.target = target;
        this.status = status;
        this.reason = reason;
        this.details = details;
    }

    public void setWishId(Integer wishId) {
        this.wishId = wishId;
    }

    public void setWishType(WishType wishType) {
        this.wishType = wishType;
    }

    public void setTarget(WishTargetType target) {
        this.target = target;
    }

    public void setStatus(WishStatusType status) {
        this.status = status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDetails(T details) {
        this.details = details;
    }

    public Integer getWishId() {
        return this.wishId;
    }

    public WishType getWishType() {
        return this.wishType;
    }

    public WishTargetType getTarget() {
        return this.target;
    }

    public WishStatusType getStatus() {
        return this.status;
    }

    public String getReason() {
        return this.reason;
    }

    public T getDetails() {
        return this.details;
    }



    public static class WishBuilder<T> {
        private Integer wishId;
        private WishType wishtype;
        private WishTargetType target;
        private WishStatusType status;
        private String reason;
        private T details;

        public WishBuilder<T> withWishId(Integer wishId) {
            this.wishId = wishId;
            return this;
        }

        public WishBuilder<T> withWishType(WishType wishType) {
            this.wishtype = wishType;
            return this;
        }

        public WishBuilder<T> withTarget(WishTargetType target) {
            this.target = target;
            return this;
        }

        public WishBuilder<T> withStatus(WishStatusType status) {
            this.status = status;
            return this;
        }

        public WishBuilder<T> withReason(String reason) {
            this.reason = reason;
            return this;
        }

        public WishBuilder<T> withDetails(T details) {
            this.details = details;
            return this;
        }

        /**
         * Build a new Wish.
         *
         * @return the new Wish.
         */
        public WishDto<T> build() {
            return new WishDto<T>(
                    this.wishId,
                    this.wishtype,
                    this.target,
                    this.status,
                    this.reason,
                    this.details
                    );
        }

    }
}