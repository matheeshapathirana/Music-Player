package info.u_team.music_player.lavaplayer.search;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;

import info.u_team.music_player.lavaplayer.MusicPlayer;
import info.u_team.music_player.lavaplayer.api.IMusicPlayerEvents.State;
import info.u_team.music_player.lavaplayer.api.ITrackSearch;
import info.u_team.music_player.lavaplayer.impl.*;
import info.u_team.music_player.lavaplayer.queue.TrackScheduler;

public class TrackSearch implements ITrackSearch {
	
	protected final AudioPlayerManager audioplayermanager;
	
	protected final TrackScheduler trackscheduler;
	
	public TrackSearch(AudioPlayerManager audioplayermanager, TrackScheduler trackscheduler) {
		this.audioplayermanager = audioplayermanager;
		this.trackscheduler = trackscheduler;
	}
	
	@Override
	public void queue(String identifier) {
		play(identifier, false);
	}
	
	@Override
	public void play(String identifier) {
		play(identifier, true);
	}
	
	public void play(String identifier, boolean force) {
		boolean search = identifier.startsWith("ytsearch:") || identifier.startsWith("scsearch:");
		audioplayermanager.loadItemOrdered(this, identifier, new AudioLoadResultHandlerSearch(identifier, force, search));
	}
	
	protected class AudioLoadResultHandlerSearch implements AudioLoadResultHandler {
		
		private final String identifier;
		private final boolean force;
		private final boolean search;
		
		public AudioLoadResultHandlerSearch(String identifier, boolean force, boolean search) {
			this.identifier = identifier;
			this.force = force;
			this.search = search;
		}
		
		@Override
		public void trackLoaded(AudioTrack track) {
			if (force) {
				playTrack(track);
			} else {
				queueTrack(track);
			}
		}
		
		@Override
		public void playlistLoaded(AudioPlaylist playlist) {
			List<AudioTrack> tracks = playlist.getTracks();
			if (tracks.size() == 0) {
				noMatches();
				return;
			}
			if (playlist.isSearchResult()) {
				AudioTrack track = tracks.get(0);
				if (force) {
					playTrack(track);
				} else {
					queueTrack(track);
				}
			} else {
				if (force) {
					playPlayList(playlist);
				} else {
					queuePlayList(playlist);
				}
			}
		}
		
		@Override
		public void noMatches() {
			if (!search) {
				play("ytsearch: " + identifier, force);
				return;
			}
			MusicPlayer.getEventHandler().forEach(event -> event.onSearchFailed("no matches", new IllegalArgumentException("no matches")));
		}
		
		@Override
		public void loadFailed(FriendlyException exception) {
			MusicPlayer.getEventHandler().forEach(event -> event.onSearchFailed(exception.getMessage(), exception));
		}
		
	}
	
	protected void playPlayList(AudioPlaylist playlist) {
		MusicPlayer.getEventHandler().forEach(event -> event.onSearchTrackList(State.PLAY, new AudioTrackListImpl(playlist)));
		List<AudioTrack> tracks = playlist.getTracks();
		trackscheduler.play(tracks.get(0));
		for (int i = tracks.size() - 1; i > 0; i--) {
			trackscheduler.queueFirst(tracks.get(i));
		}
	}
	
	protected void queuePlayList(AudioPlaylist playlist) {
		MusicPlayer.getEventHandler().forEach(event -> event.onSearchTrackList(State.QUEUE, new AudioTrackListImpl(playlist)));
		for (AudioTrack track : playlist.getTracks()) {
			trackscheduler.queueLast(track);
		}
	}
	
	protected void playTrack(AudioTrack track) {
		MusicPlayer.getEventHandler().forEach(event -> event.onSearchTrack(State.PLAY, new AudioTrackImpl(track)));
		trackscheduler.play(track);
	}
	
	protected void queueTrack(AudioTrack track) {
		MusicPlayer.getEventHandler().forEach(event -> event.onSearchTrack(State.QUEUE, new AudioTrackImpl(track)));
		trackscheduler.queueLast(track);
	}
	
}
