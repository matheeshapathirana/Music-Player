package info.u_team.music_player.musicplayer.settings;

import info.u_team.music_player.musicplayer.MusicPlayerManager;

public class Settings {

	private int volume;

	private Repeat repeat;

	private boolean shuffle;

	private boolean showIngameOverlay;
	private boolean showIngameMenueOverlay;
	
	

	public Settings() {
		volume = 10;
		repeat = Repeat.NO;
		shuffle = false;
		showIngameOverlay = true;
		showIngameMenueOverlay = false;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
		save();
	}

	public Repeat getRepeat() {
		return repeat;
	}

	public void setRepeat(Repeat repeat) {
		this.repeat = repeat;
		save();
	}

	public boolean isShuffle() {
		return shuffle;
	}

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
		save();
	}

	public boolean isShowIngameOverlay() {
		return showIngameOverlay;
	}

	public void setShowIngameOverlay(boolean showIngameOverlay) {
		this.showIngameOverlay = showIngameOverlay;
		save();
	}

	public boolean isShowIngameMenueOverlay() {
		return showIngameMenueOverlay;
	}

	public void setShowIngameMenueOverlay(boolean showIngameMenueOverlay) {
		this.showIngameMenueOverlay = showIngameMenueOverlay;
		save();
	}

	public boolean isFinite() {
		return repeat == Repeat.NO;
	}

	public boolean isSingleRepeat() {
		return repeat == Repeat.SINGLE;
	}

	private void save() {
		MusicPlayerManager.getSettingsManager().writeToFile();
	}
}
