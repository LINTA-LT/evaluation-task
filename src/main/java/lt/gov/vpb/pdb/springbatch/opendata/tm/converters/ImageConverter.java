package lt.gov.vpb.pdb.springbatch.opendata.tm.converters;

import lombok.extern.slf4j.Slf4j;
import lt.vpb.open_data.schemas.trademark.v1_2.MarkImageCategoryType;
import lt.vpb.open_data.schemas.trademark.v1_2.MarkImageCategoryType.CategoryCodeDetails;
import lt.vpb.open_data.schemas.trademark.v1_2.MarkImageType;
import lt.vpb.open_data.schemas.trademark.v1_2.TradeMarkType.MarkImageDetails;
import lt.vpb.open_data.schemas.trademark.v1_2.URIType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;
import org.tmdn.cti.repository.jpa.domain.trademark.ImageCategory;
import org.tmdn.cti.repository.jpa.domain.trademark.TmImage;

import static org.apache.commons.lang.StringUtils.isEmpty;

@Component("opendata.tm.imageConverter")
@Slf4j
public class ImageConverter {

	@Value("${opendata.tm.image-uri-template}")
	private UriTemplate imageUriTemplate;

	public MarkImageDetails convert(TmImage tmImage, String applicationNumber) {
		MarkImageDetails imageDetails = new MarkImageDetails();
		MarkImageType imageType = new MarkImageType();
		imageType.setMarkImageURI(buildImageURI(applicationNumber));
		if(StringUtils.isNotBlank(tmImage.getColourClaimedText())) {
			imageType.setMarkImageColourClaimedText(tmImage.getColourClaimedText());
		}
		if(tmImage.getImageCategories()!=null && !tmImage.getImageCategories().isEmpty()) {
			imageType.setMarkImageCategory(buildImageCategory(tmImage));
		}
		imageDetails.setMarkImage(imageType);

		return imageDetails;
	}

	private MarkImageCategoryType buildImageCategory(TmImage tmImage) {
		MarkImageCategoryType imageCategoryType = new MarkImageCategoryType();
		CategoryCodeDetails catCodeDetails = new CategoryCodeDetails();

		for (ImageCategory imgCateg : tmImage.getImageCategories()) {
			if(StringUtils.isNotBlank(imgCateg.getCategoryCode()) && imgCateg.isCategoryCodeValid()) {
				String catCode = imgCateg.getCategoryCode();
				if(!isEmpty(catCode)){
					catCodeDetails.getCategoryCode().add(catCode);
				}
			}
		}
		imageCategoryType.setCategoryCodeDetails(catCodeDetails);
		return imageCategoryType;
	}

	private URIType buildImageURI(String applicationNumber) {
		URIType imageURI = new URIType();
		imageURI.setValue(imageUriTemplate.expand(applicationNumber).toString());
		return imageURI;
	}

}
