export interface ResponseData {
	body: ResponseBody;
	headers: ResponseHeaders;
	statusCode: number;
}

interface ResponseBody {
	tracks: PagingObject<TrackSearchItem>;
}

interface ResponseHeaders {
	'content-type': string;
	'cache-control': string;
	'x-robots-tag': string;
	'access-control-allow-origin': string;
	'access-control-allow-headers': string;
	'access-control-allow-methods': string;
	'access-control-allow-credentials': string;
	'access-control-max-age': string;
	'content-encoding': string;
	'strict-transport-security': string;
	'x-content-type-options': string;
	date: string;
	server: string;
	via: string;
	'alt-svc': string;
	connection: string;
	'transfer-encoding': string;
}

interface PagingObject<T> {
	href: string;
	items: T[];
	limit: number;
	next: string | null;
	offset: number;
	previous: string | null;
	total: number;
}

export interface TrackSearchItem extends Metadata {
	album: TrackSearchAlbum;
	artists: Array<Metadata>;
	disc_number: number;
	duration_ms: number;
	explicit: boolean;
	external_ids: ExternalObject;
	is_local: boolean;
	is_playable: boolean;
	popularity: number;
	preview_url: string | null;
	track_number: number;
}

interface TrackSearchAlbum extends Metadata {
	album_type: string;
	artists: Array<Metadata>;
	images: Array<ImageObject>;
	release_date: string;
	release_date_precision: string;
	total_tracks: number;
}

interface Metadata {
	external_urls: ExternalObject;
	href: string;
	id: string;
	name: string;
	type: string;
	uri: string;
}

interface ExternalObject {
	[key: string]: string;
}

interface ImageObject {
	height: number;
	url: string;
	width: number;
}
