package thaumcraft.api.aspects;

import net.minecraftforge.common.util.ForgeDirection;

public interface IUntypedEssentiaTransport {
    int getUntypedSuctionAmount(ForgeDirection loc);

    int getTypedSuctionAmount(ForgeDirection loc);

    Aspect getTypedSuctionType(ForgeDirection loc);

    int getUntypedEssentiaAmount(ForgeDirection loc);

    Aspect getUntypedEssentiaType(ForgeDirection loc);

    int getTypedEssentiaAmount(ForgeDirection loc);

    Aspect getTypedEssentiaType(ForgeDirection loc);
}
