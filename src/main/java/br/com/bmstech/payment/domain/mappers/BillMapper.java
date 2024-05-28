package br.com.bmstech.payment.domain.mappers;

import br.com.bmstech.payment.domain.dto.BillRequetDTO;
import br.com.bmstech.payment.domain.dto.BillResponseDTO;
import br.com.bmstech.payment.domain.entity.Bill;
import org.mapstruct.Mapper;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface BillMapper {

    Bill mapDtoToEntity(BillRequetDTO billRequetDTO);

    BillResponseDTO mapEntityToDto(Bill bill);
}
