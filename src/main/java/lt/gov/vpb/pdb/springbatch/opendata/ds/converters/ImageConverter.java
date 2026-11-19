package lt.gov.vpb.pdb.springbatch.opendata.ds.converters;

import lt.vpb.open_data.schemas.design.v1_2.DesignRepresentationSheetType.ViewDetails;
import lt.vpb.open_data.schemas.design.v1_2.URIType;
import lt.vpb.open_data.schemas.design.v1_2.ViewType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;
import org.tmdn.cti.repository.jpa.domain.design.Design;
import org.tmdn.cti.repository.jpa.domain.design.DesignImage;

import java.util.ArrayList;
import java.util.List;


@Component
public class ImageConverter {

	@Value("${opendata.ds.image-uri-template}")
	private UriTemplate imageUriTemplate;

	protected ViewDetails wrap(List<ViewType> list) {
		return new ViewDetails().withView(list);
	}

	public ViewType convert(DesignImage object, Design design, String designSt13) {
		ViewType viewType = new ViewType();
		viewType.setViewURI(new URIType().withValue(
				imageUriTemplate.expand(
						generateSt13(object, design, designSt13)
				).toString()));

		return viewType;
	}
	
	
	private String generateSt13(DesignImage object, Design design,
			String designSt13) {
		if (!design.getImages().contains(object)) {
			throw new IllegalStateException(
					"DesignImage is not a part of the design");
		}
		return designSt13
				+ "-"
				+ StringUtils.leftPad(String.valueOf((design.getImages()
						.indexOf(object) + 1)), 3, "0");
	}

	public ViewDetails convert(
			List<DesignImage> object, Design design, String designSt13) {
		List<ViewType> list = new ArrayList<>();
		if (object != null) {
			for (DesignImage o : object) {
				if (o != null) {
					list.add(convert(o, design, designSt13));
				}
			}
		}

		return wrap(list);
	}



}
