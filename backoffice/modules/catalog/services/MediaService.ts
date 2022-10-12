import { Media } from "../models/Media";

export async function uploadMedia(image: File): Promise<Media> {
    const body = new FormData();
    body.append("multipartFile", image);
    const response = await fetch('/api/media/medias', {
        method: 'POST',
        body: body,
    })
    return await response.json();
}