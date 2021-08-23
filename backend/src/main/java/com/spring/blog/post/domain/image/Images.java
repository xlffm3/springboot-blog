package com.spring.blog.post.domain.image;

import com.spring.blog.post.domain.Post;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Images {

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Image> images;

    protected Images() {
    }

    public Images(List<Image> images) {
        this.images = images;
    }

    public void add(List<String> imageUrls, Post post) {
        for (String imageUrl : imageUrls) {
            Image image = new Image(imageUrl, post);
            images.add(image);
        }
    }

    public List<String> getUrls() {
        return images.stream()
            .map(Image::getUrl)
            .collect(Collectors.toList());
    }
}
