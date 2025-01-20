using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ManifestPortal1.Entities.Models;

namespace ManifestPortal1.DataAccess.Repository.Contracts
{
    public interface IGuestRepository
    {

        Task<Guest> GetGuestByIdAsync(int id);
        Task<IEnumerable<Guest>> GetAllGuestsAsync();
        Task<Guest> AddGuestAsync(Guest guest);

        Task UpdateGuestAsync(Guest guest);

        Task UpdateGuestForSoftDeleteAsync(Guest guest);
        Task DeleteGuestAsync(int id);
        Task DeleteMultipleGuestAsync(int id);
        Task<List<Guest>> SearchGuestsAsync(DateTime? embarkDate, DateTime? dbarkDate);

        Task<List<Guest>> GetGuestsByTitleAsync(string salutation);
        Task<List<Guest>> SearchByDebarkDateAsync(DateTime dbarkDate);
        Task<List<Guest>> SearchByEmbarkDateAsync(DateTime embarkDate);

        Task<List<Guest>> SearchByDebarkDate(String dearkDate);
        Task<List<Guest>> SearchByEmbarkDate(String embarkDate);

        Task<Guest> GetGuestByBarcodeAsync(int barcode);
          Task<Guest> GetGuestByEmailAsync(String email);
        Task<Guest?> GetGuestOperatorByIdAsync(int guestId);
        Task UpdateGuestOperatorAsync(Guest guestOperator);

    }
}

