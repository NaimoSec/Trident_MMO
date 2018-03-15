package org.trident.world.content.audio;

import org.trident.model.Position;
import org.trident.world.entity.player.Player;

/**
 * @author Gabbe
 * Credits to Faris for configs
 */
public class Music {

	/**
	 * Updates a players song when they changed region
	 * @param player
	 */
	public static void update(Player player) {
		int songId = getSongID(getAreaID(player));
		if(songId > 0)
			player.getPacketSender().sendSong(songId);
	}

	 /**
     * takes area IDs as switch, and returns the song ID for that area
     * @param area
     * @return
     */
    public static int getSongID(int area) {
    
        switch(area){
            case 1:
                return 62;
            case 2:
                return 318;
            case 3:
            	return 381;
            case 4:
            	return 380;
            case 5:
            	return 96;
            case 6:
            	return 99;
            case 7:
            	return 98;
            case 8:
            	return 3;
            case 9:
            	return 587;
            case 10:
            	return 50;
            case 11:
                return 76;
            case 12:
            	return 72;
            case 13:
            	return 473;
            case 14:
            	//TODO find pirate music
            case 15:
            	return 141;
            case 16:
            	//TODO find abbys music
            case 17:
            	return 172;
            case 18:
            	//TODO find bandit camp music
            case 19:
            	return 66;
            case 20:
            	//TODO find bedibin camp music lol wut
            case 21:
            	//TODO find the song "morooned"
            case 22:
            	return 119;
            case 23:
            	return 87;
            case 24:
            	//TODO find burthrope music
            case 25:
            	return 104;
            case 26:
            	//TODO find canifis "village" music
            case 27:
            	//TODO find maze music (random
            case 28:
            	//TODO find cranador music lol
            case 29:
            	return 151;
            case 30:
            	return 47;
            case 31:
            	return 179;
            case 32:
            	return 150;
            case 33:
            	return 23;
            case 34:
            	//TODO find ham hideout music
            case 35:
            	return 114;
            case 36:
            	return 412;
            case 37:
            	//TODO find misc music
            case 38:
            	return 286;
            case 39:
            	//TODO find port Phasmatys music
            case 40:
            	return 35;
            case 41:
            	//TODO find long way home song
            case 42:
            	return 7;
            case 43:
            	return 90;
            case 44:
            	return 18;
            case 45:
            	return 23;
            case 46:
            	return 469;
            case 47:
            	return 125;
            case 48:
            	return 185;
            case 49:
            	return 314;
            case 50:
            	return 318;
            case 51:
            	return 318;
            case 52:
            	return 28;
            case 53:
            	//TODO find kalphite queen music
            case 54:
            	return 2;
            case 55:
            	return 111;
            case 56:
            	return 123;
            case 57:
            	return 36;
            case 58:
            	return 122;
            case 59:
            	return 541;
            case 60:
            	return 64;
            case 61:
            	return 327;
            case 62:
            	return 163;
            case 63:
            	return 333;
            case 64:
            	return 116;
            case 65:
            	return 157;
            case 66:
            	return 177;
            case 67:
            	return 93;
            case 68:
            	return 48;
            case 69:
            	return 107;
            case 70:
            	return 49;
            case 71:
            	return 186;
            default:
                return 3;
        }
    }
    
	/**
	 * Sets area id by integer in MusicHandler
	 * @param c
	 * @return
	 */
	public static int getAreaID(Player player) {
		final Position location = player.getPosition().copy();
		if (location.getX() >= 2625 && location.getX() <= 2687 && location.getY() >= 4670 && location.getY() <= 4735) 
			return 1;
		if ((location.getX() >= 2368 && location.getX() <= 2376 && location.getY() >= 3127 && location.getY() <= 3135 && location.getZ() == 1) || (location.getX() >= 2423 && location.getX() <= 2431 && location.getY() >= 3072 && location.getY() <= 3080 && location.getZ() == 1)) 
			return 2;
		if (location.getX() > 3520 && location.getX() < 3598 && location.getY() > 9653 && location.getY() < 9750) 
			return 3;
		if (location.getX() >= 3542 && location.getX() <= 3583 && location.getY() >= 3265 && location.getY() <= 3322) 
			return 4;
		if(location.getX() > 2941 && location.getX() < 3392 && location.getY() > 3518 && location.getY() < 3966 ||
				location.getX() > 3343 && location.getX() < 3384 && location.getY() > 9619 && location.getY() < 9660 ||
				location.getX() > 2941 && location.getX() < 3392 && location.getY() > 9918 && location.getY() < 10366) 	
			return 5;
		if (location.getX() > 2558 && location.getX() < 2729 && location.getY() > 3263 && location.getY() < 3343) 
			return 6;
		if (location.getX() > 3084 && location.getX() < 3111 && location.getY() > 3483 && location.getY() < 3509) 
			return 7;
		if (location.getX() > 2935 && location.getX() < 3061 && location.getY() > 3308 && location.getY() < 3396) 
			return 8;
		if (location.getX() >= 2659 && location.getX() <= 2664 && location.getY() >= 2637 && location.getY() <= 2644 || location.getX() >= 2623 && location.getX() <= 2690 && location.getY() >= 2561 && location.getY() <= 2688) 
			return 9;
		if (location.getX() > 3270 && location.getX() < 3455 && location.getY() > 2880 && location.getY() < 3330) 
			return 10;
		if (location.getX() > 3187 && location.getX() < 3253 && location.getY() > 3189 && location.getY() < 3263) 
			return 11;
		if (location.getX() > 3002 && location.getX() < 3004 && location.getY() > 3002 && location.getY() < 3004) 
			return 12;
		if (location.getX() >= 2360 && location.getX() <= 2445 && location.getY() >= 5045 && location.getY() <= 5125) 
			return 13;
		if (location.getX() >= 3038 && location.getX() <= 3044 && location.getY() >= 3949 && location.getY() <= 3959) 
			return 14;
		if (location.getX() >= 3060 && location.getX() <= 3099 && location.getY() >= 3399 && location.getY() <= 3450) 
			return 15;
		if (location.getX() >= 3008 && location.getX() <= 3071 && location.getY() >= 4800 && location.getY() <= 4863) 
			return 16;
		if (location.getX() >= 2691 && location.getX() <= 2826 && location.getY() >= 2690 && location.getY() <= 2831) 
			return 17;
		if (location.getX() >= 3155 && location.getX() <= 3192 && location.getY() >= 2962 && location.getY() <= 2994) 
			return 18;
		if (location.getX() >= 2526 && location.getX() <= 2556 && location.getY() >= 3538 && location.getY() <= 3575) 
			return 19;
		if (location.getX() >= 3165 && location.getX() <= 3199 && location.getY() >= 3022 && location.getY() <= 3054) 
			return 20;
		if (location.getX() >= 2785 && location.getX() <= 2804 && location.getY() >= 3312 && location.getY() <= 3327) 
			return 21;
		if ((location.getX() >= 2792 && location.getX() <= 2829 && location.getY() >= 3412 && location.getY() <= 3472) ||
				(location.getX() > 2828 && location.getX() < 2841 && location.getY() > 3430 && location.getY() < 3459) ||
				(location.getX() > 2839 && location.getX() < 2861 && location.getY() > 3415 && location.getY() < 3441))
			return 22;
		if (location.getX() >= 2850 && location.getX() <= 2879 && location.getY() >= 3446 && location.getY() <= 3522)
			return 23;
		if (location.getX() >= 2878 && location.getX() <= 2937 && location.getY() >= 3524 && location.getY() <= 3582) 
			return 24;
		if (location.getX() >= 2744 && location.getX() <= 2787 && location.getY() >= 3457 && location.getY() <= 3519) 
			return 25;
		if (location.getX() >= 3425 && location.getX() <= 3589 && location.getY() >= 3256 && location.getY() <= 3582) 
			return 26;
		if (location.getX() >= 2883 && location.getX() <= 2942 && location.getY() >= 4547 && location.getY() <= 4605) 
			return 27;
		if (location.getX() >= 2819 && location.getX() <= 2859 && location.getY() >= 3224 && location.getY() <= 3312) 
			return 28;
		if (location.getX() >= 3067 && location.getX() <= 3134 && location.getY() >= 3223 && location.getY() <= 3297) 
			return 29;
		if (location.getX() >= 3324 && location.getX() <= 3392 && location.getY() >= 3196 && location.getY() <= 3329) 
			return 30;
		if (location.getX() >= 2800 && location.getX() <= 2869 && location.getY() >= 3324 && location.getY() <= 3391) 
			return 31;
		if (location.getX() >= 2492 && location.getX() <= 2563 && location.getY() >= 3132 && location.getY() <= 3203) 
			return 32;
		if (location.getX() >= 2945 && location.getX() <= 2968 && location.getY() >= 3477 && location.getY() <= 3519) 
			return 33;
		if (location.getX() >= 3136 && location.getX() <= 3193 && location.getY() >= 9601 && location.getY() <= 9664) 
			return 34;
		if (location.getX() >= 2816 && location.getX() <= 2958 && location.getY() >= 3139 && location.getY() <= 3175) 
			return 35;
		if (location.getX() >= 2334 && location.getX() <= 2341 && location.getY() >= 4743 && location.getY() <= 4751) 
			return 36;
		if (location.getX() >= 2495 && location.getX() <= 2625 && location.getY() >= 3836 && location.getY() <= 3905) 
			return 37;
		if (location.getX() >= 3465 && location.getX() <= 3520 && location.getY() >= 3266 && location.getY() <= 3309) 
			return 38;
		if (location.getX() >= 3585 && location.getX() <= 3705 && location.getY() >= 3462 && location.getY() <= 3539) 
			return 39;
		if (location.getX() >= 2985 && location.getX() <= 3064 && location.getY() >= 3164 && location.getY() <= 3261) 
			return 40;
		if (location.getX() >= 2913 && location.getX() <= 2989 && location.getY() >= 3185 && location.getY() <= 3267) 
			return 41;
		if (location.getX() >= 2639 && location.getX() <= 2740 && location.getY() >= 3391 && location.getY() <= 3503) 
			return 42;
		if (location.getX() >= 2816 && location.getX() <= 2879 && location.getY() >= 2946 && location.getY() <= 3007) 
			return 43;
		if (location.getX() >= 2874 && location.getX() <= 2934 && location.getY() >= 3390 && location.getY() <= 3492) 
			return 44;
		if (location.getX() >= 2413 && location.getX() <= 2491 && location.getY() >= 3386 && location.getY() <= 3515) 
			return 45;
		if (location.getX() >= 2431 && location.getX() <= 2495 && location.getY() >= 5117 && location.getY() <= 5180) 
			return 46;
		if (location.getX() >= 3168 && location.getX() <= 3291 && location.getY() >= 3349 && location.getY() <= 3514) 
			return 47;
		if (location.getX() >= 2532 && location.getX() <= 2621 && location.getY() >= 3071 && location.getY() <= 3112) 
			return 48;
		if (location.getX() >= 2368 && location.getX() <= 2430 && location.getY() >= 3073 && location.getY() <= 3135) 
			return 49;
		if (location.getX() >= 2440 && location.getX() <= 2444 && location.getY() >= 3083 && location.getY() <= 3095) 
			return 50;
		if (location.getX() >= 2359 && location.getX() <= 2440 && location.getY() >= 9466 && location.getY() <= 9543) 
			return 51;
		if (location.getX() >= 2251 && location.getX() <= 2295 && location.getY() >= 4675 && location.getY() <= 4719) 
			return 52;
		if (location.getX() >= 3463 && location.getX() <= 3515 && location.getY() >= 9469 && location.getY() <= 9524) 
			return 53;
		if (location.getX() >= 3200 && location.getX() <= 3303 && location.getY() >= 3273 && location.getY() <= 3353) 
			return 54;;
			if (location.getX() >= 3274 && location.getX() <= 3328 && location.getY() >= 3315 && location.getY() <= 3353) 
				return 55;
			if (location.getX() >= 3274 && location.getX() <= 3266 && location.getY() >= 3323 && location.getY() <= 3327) 
				return 56;
			if (location.getX() >= 3274 && location.getX() <= 3200 && location.getY() >= 3323 && location.getY() <= 3265) 
				return 57;
			if (location.getX() >= 3324 && location.getX() <= 3263 && location.getY() >= 3408 && location.getY() <= 3285) 
				return 58;
			if (location.getX() >= 3324 && location.getX() <= 3286 && location.getY() >= 3408 && location.getY() <= 3327) 
				return 59;
			if (location.getX() >= 3136 && location.getX() <= 3136 && location.getY() >= 3193 && location.getY() <= 3199) 
				return 60; 
			if (location.getX() >= 3121 && location.getX() <= 3200 && location.getY() >= 3199 && location.getY() <= 3268) 
				return 61; 
			if (location.getX() >= 3121 && location.getX() <= 3269 && location.getY() >= 3199 && location.getY() <= 3314)
				return 62;
			if (location.getX() >= 3066 && location.getX() <= 3315 && location.getY() >= 3147 && location.getY() <= 3394) 
				return 63;
			if (location.getX() >= 3200 && location.getX() <= 3354 && location.getY() >= 3315 && location.getY() <= 3394) 
				return 64;
			if (location.getX() >= 3248 && location.getX() <= 3395 && location.getY() >= 3328 && location.getY() <= 3468) 
				return 65;
			if (location.getX() >= 3111 && location.getX() <= 3469 && location.getY() >= 3264 && location.getY() <= 3524) 
				return 66;
			if (location.getX() >= 3265 && location.getX() <= 3469 && location.getY() >= 3328 && location.getY() <= 3524) 
				return 67;
			if (location.getX() >= 3329 && location.getX() <= 3447 && location.getY() >= 3418 && location.getY() <= 3524) 
				return 68;
			if (location.getX() >= 2889 && location.getX() <= 3265 && location.getY() >= 2940 && location.getY() <= 3324) 
				return 69;
			if (location.getX() >= 3014 && location.getX() <= 3261 && location.getY() >= 3065 && location.getY() <= 3324) 
				return 70; 
			if (location.getX() >= 2880 && location.getX() <= 3325 && location.getY() >= 2935 && location.getY() <= 3394) 
				return 71; 
			return 0;
	}
}
