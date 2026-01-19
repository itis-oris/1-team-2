package com.tourlist.service;

import com.tourlist.model.RoomOption;
import java.util.List;

public interface RoomOptionService {

    RoomOption getRoomOptionById(int id);
    List<RoomOption> getAllRoomOptions();
    List<RoomOption> getRoomOptionsByTour(int tourId);
    boolean createRoomOption(RoomOption option);
    boolean updateRoomOption(RoomOption option);
    boolean deleteRoomOption(int id);
}
