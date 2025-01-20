using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ManifestPortal1.Entities.DTO;
using ManifestPortal1.Entities.Models;
using Microsoft.AspNetCore.Mvc;
using MySqlX.XDevAPI.Common;

namespace ManifestPortal1.Business.Contracts
{
    public interface IGuestService
    {

        Task<Guest> GetGuestAsync(int id);
        Task<IEnumerable<Guest>> GetAllGuestsAsync();
        Task<Guest> RegisterGuestAsync(AddGuestDTO guestDto);
        Task UpdateGuestAsync(int id, UpdateGuestDTO guest);
        Task DeleteGuestAsync(int id);
        Task DeleteGuestsAsync(List<int> ids);
        Task<List<Guest>> SearchGuestsAsync(DateTime? embarkDate, DateTime? dbarkDate);
        Task<List<Guest>> GetGuestsBySalutationAsync(string salutation);
        Task<ActionResult> CheckInGuestAsync(int barcode);
        Task<ActionResult> CheckOutGuestAsync(int barcode);
        Task<List<Guest>> SearchByEmbarkDateAsync(DateTime embarkDate);
        Task<List<Guest>> SearchByDebarkDateAsync(DateTime debarkDate);
        Task<List<Guest>> SerachByEmbarkDate(String embarkDate);
        Task<List<Guest>> SerachByDebarkDate(String dearkDate);
         Task SoftDeleteGuestAsync(int id);
        Task SoftMultipleDeleteGuestAsync(List<int> ids);
         Task<ServiceResponseDTO> UpdateGuestImageAsync(int crewId, UpdateGuestImageRequestDTO request);
    }
}
