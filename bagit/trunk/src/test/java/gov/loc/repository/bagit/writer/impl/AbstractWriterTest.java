package gov.loc.repository.bagit.writer.impl;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.bag.DummyCancelIndicator;
import gov.loc.repository.bagit.bag.PrintingProgressIndicator;
import gov.loc.repository.bagit.utilities.ResourceHelper;
import gov.loc.repository.bagit.writer.Writer;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractWriterTest {

	@Before
	public void setUp() throws Exception {
		if (this.getBagFile().exists()) {
			FileUtils.forceDelete(this.getBagFile());
		}
	}

	public abstract Writer getBagWriter();
	
	public abstract File getBagFile();
	
	@Test
	public void testWriter() throws Exception {
		Bag bag = BagFactory.createBag(ResourceHelper.getFile("bags/v0_95/bag"));
		assertTrue(bag.checkValid().isSuccess());
		Writer writer = this.getBagWriter();
		writer.setProgressIndicator(new PrintingProgressIndicator());
		
		Bag newBag = writer.write(bag);
		assertNotNull(newBag);
		assertTrue(this.getBagFile().exists());
		assertTrue(newBag.checkValid().isSuccess());
		
		List<Manifest> payloadManifests = newBag.getPayloadManifests();
		assertEquals(1, payloadManifests.size());
		assertEquals("manifest-md5.txt", payloadManifests.get(0).getFilepath());
		assertEquals(4, newBag.getTags().size());
		assertNotNull(newBag.getBagFile("bagit.txt"));
		
		assertEquals(5, newBag.getPayload().size());
		assertNotNull(newBag.getBagFile("data/dir1/test3.txt"));
		
	}

	@Test
	public void testCancel() throws Exception {
		Bag bag = BagFactory.createBag(ResourceHelper.getFile("bags/v0_95/bag"));
		assertTrue(bag.checkValid().isSuccess());
		
		Writer writer = this.getBagWriter();
		writer.setCancelIndicator(new DummyCancelIndicator(3));		
		
		Bag newBag = writer.write(bag);
		assertNull(newBag);
		
	}

}
