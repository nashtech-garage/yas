import {
  ToastVariant,
  ResponseStatus,
  ResponseTitle,
  HAVE_BEEN_DELETED,
  DELETE_FAILED,
  UPDATE_SUCCESSFULLY,
  CREATE_SUCCESSFULLY,
  UPDATE_FAILED,
  CREATE_FAILED,
  DEFAULT_PAGE_SIZE,
  DEFAULT_PAGE_NUMBER,
  FORMAT_DATE_YYYY_MM_DD_HH_MM,
  mappingExportingProductColumnNames,
} from '../../constants/Common';

describe('Constants/Common', () => {
  describe('ToastVariant', () => {
    it('should have SUCCESS value', () => {
      expect(ToastVariant.SUCCESS).toBe('success');
    });

    it('should have WARNING value', () => {
      expect(ToastVariant.WARNING).toBe('warning');
    });

    it('should have ERROR value', () => {
      expect(ToastVariant.ERROR).toBe('error');
    });
  });

  describe('ResponseStatus', () => {
    it('should have CREATED status 201', () => {
      expect(ResponseStatus.CREATED).toBe(201);
    });

    it('should have SUCCESS status 204', () => {
      expect(ResponseStatus.SUCCESS).toBe(204);
    });

    it('should have NOT_FOUND status 404', () => {
      expect(ResponseStatus.NOT_FOUND).toBe(404);
    });

    it('should have BAD_REQUEST status 400', () => {
      expect(ResponseStatus.BAD_REQUEST).toBe(400);
    });
  });

  describe('ResponseTitle', () => {
    it('should have NOT_FOUND title', () => {
      expect(ResponseTitle.NOT_FOUND).toBe('Not Found');
    });

    it('should have BAD_REQUEST title', () => {
      expect(ResponseTitle.BAD_REQUEST).toBe('Bad Request');
    });
  });

  describe('Message constants', () => {
    it('HAVE_BEEN_DELETED should be correct', () => {
      expect(HAVE_BEEN_DELETED).toBe(' have been deleted');
    });

    it('DELETE_FAILED should be correct', () => {
      expect(DELETE_FAILED).toBe('Delete failed');
    });

    it('UPDATE_SUCCESSFULLY should be correct', () => {
      expect(UPDATE_SUCCESSFULLY).toBe('Update successfully');
    });

    it('CREATE_SUCCESSFULLY should be correct', () => {
      expect(CREATE_SUCCESSFULLY).toBe('Create successfully');
    });

    it('UPDATE_FAILED should be correct', () => {
      expect(UPDATE_FAILED).toBe('Update failed');
    });

    it('CREATE_FAILED should be correct', () => {
      expect(CREATE_FAILED).toBe('Create failed');
    });
  });

  describe('Pagination constants', () => {
    it('DEFAULT_PAGE_SIZE should be 10', () => {
      expect(DEFAULT_PAGE_SIZE).toBe(10);
    });

    it('DEFAULT_PAGE_NUMBER should be 0', () => {
      expect(DEFAULT_PAGE_NUMBER).toBe(0);
    });
  });

  describe('FORMAT_DATE_YYYY_MM_DD_HH_MM', () => {
    it('should have correct date format string', () => {
      expect(FORMAT_DATE_YYYY_MM_DD_HH_MM).toBe('YYYYMMDDHHmmss');
    });
  });

  describe('mappingExportingProductColumnNames', () => {
    it('should map id to Id', () => {
      expect(mappingExportingProductColumnNames.id).toBe('Id');
    });

    it('should map name to Product Name', () => {
      expect(mappingExportingProductColumnNames.name).toBe('Product Name');
    });

    it('should map sku to SKU', () => {
      expect(mappingExportingProductColumnNames.sku).toBe('SKU');
    });

    it('should map price to Price', () => {
      expect(mappingExportingProductColumnNames.price).toBe('Price');
    });

    it('should have all required keys', () => {
      const keys = Object.keys(mappingExportingProductColumnNames);
      expect(keys).toContain('id');
      expect(keys).toContain('name');
      expect(keys).toContain('sku');
      expect(keys).toContain('price');
      expect(keys).toContain('brandId');
    });
  });
});
